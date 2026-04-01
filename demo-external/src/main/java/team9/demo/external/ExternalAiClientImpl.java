package team9.demo.external;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import team9.demo.dto.ChatGPTRequest;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.model.ai.analysis.*;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.user.UserId;
import team9.demo.repository.ai.ExternalAiClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalAiClientImpl implements ExternalAiClient {

    @Value("${openai.api.url}")
    private String visionEndpoint;

    @Value("${openai.api.url2}")
    private String chatEndpoint;

    @Value("${openai.api.url.image}")
    private String editsEndpoint;

    @Value("${openai.model1}")
    private String model;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${ai.yolo.url:http://localhost:5000}")
    private String yoloServerUrl;

    @Value("${ai.lama.url:http://localhost:7870}")
    private String lamaServerUrl;

    private final RestTemplate restTemplate;
    private final AmazonS3 amazonS3;

    // [1] GPT Vision 단일 이미지 분석
    @Override
    public ChatResponse requestImageAnalysis(String imageUrl, String requestText) {
        try {
            String base64 = encodeImageFromS3(imageUrl);
            String dataUrl = "data:image/jpeg;base64," + base64;

            ChatGPTRequest requestDto = ChatGPTRequest.of(model, requestText, dataUrl, 500);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    chatEndpoint, HttpMethod.POST, new HttpEntity<>(requestDto), getMapType()
            );
            return parseChatResponse(response);

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("OpenAI 사용량 초과: {}", e.getResponseBodyAsString());
            throw new AiException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
        } catch (IOException e) {
            throw new AiException(ErrorCode.AI_IMAGE_READ_FAILED);
        }
    }

    // [2] GPT Vision 비교 분석 (Before/After)
    @Override
    public ChatResponse requestCompareAnalysis(String beforeUrl, String afterUrl, String requestText) {
        try {
            String beforeBase64 = encodeImageFromS3(beforeUrl);
            String afterBase64 = encodeImageFromS3(afterUrl);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text", "text", requestText),
                                    Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + beforeBase64)),
                                    Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + afterBase64))
                            )
                    )),
                    "max_tokens", 500
            );

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    chatEndpoint, HttpMethod.POST, new HttpEntity<>(requestBody), getMapType()
            );
            return parseChatResponse(response);

        } catch (Exception e) {
            log.error("비교 분석 실패", e);
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }
    }

    // [3] YOLO - 어질러진 영역 감지
    @Override
    public List<Box> detectClutterBoxes(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ResponseEntity<List> response = restTemplate.exchange(
                yoloServerUrl + "/yolo",
                HttpMethod.POST,
                new HttpEntity<>(imageBytes, headers),
                List.class
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rawBoxes = (List<Map<String, Object>>) response.getBody();
        if (rawBoxes == null) return Collections.emptyList();

        return rawBoxes.stream()
                .map(map -> new Box(
                        ((Number) map.get("x")).intValue(),
                        ((Number) map.get("y")).intValue(),
                        ((Number) map.get("width")).intValue(),
                        ((Number) map.get("height")).intValue()
                ))
                .toList();
    }

    // [4] LAMA Inpainting - 이미지 정리
    @Override
    public String editImageWithLama(byte[] imageBytes, byte[] maskBytes, String prompt, UserId userId) {
        MultiValueMap<String, Object> body = buildLamaRequestBody(imageBytes, maskBytes, prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        RestTemplate lamaClient = new RestTemplate(List.of(
                new FormHttpMessageConverter(),
                new ByteArrayHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()
        ));

        ResponseEntity<byte[]> response = lamaClient.exchange(
                lamaServerUrl + "/inpaint",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                byte[].class
        );

        return uploadToS3(response.getBody(), userId);
    }

    // S3 업로드
    public String uploadToS3(byte[] image, UserId userId) {
        String key = "AI/cleaned/" + userId.getId() + "/" + UUID.randomUUID() + ".png";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType("image/png");

        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(bucket, key, inputStream, metadata);
        } catch (IOException e) {
            log.error("S3 업로드 실패: {}", e.getMessage(), e);
            throw new AiException(ErrorCode.AI_S3_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, key).toString();
    }

    // === Helper Methods ===

    private byte[] downloadImageFromS3(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        String host = url.getHost();
        String path = url.getPath();

        String key;
        if (path.startsWith("/" + bucket + "/")) {
            key = path.substring(("/" + bucket + "/").length());
        } else if (host.startsWith(bucket + ".")) {
            key = path.startsWith("/") ? path.substring(1) : path;
        } else {
            throw new AiException(ErrorCode.AI_IMAGE_READ_FAILED);
        }

        S3Object object = amazonS3.getObject(bucket, key);
        try (InputStream inputStream = object.getObjectContent()) {
            return inputStream.readAllBytes();
        }
    }

    private String encodeImageFromS3(String imageUrl) throws IOException {
        return Base64.getEncoder().encodeToString(downloadImageFromS3(imageUrl));
    }

    @SuppressWarnings("unchecked")
    private ChatResponse parseChatResponse(ResponseEntity<Map<String, Object>> response) {
        Map<String, Object> body = response.getBody();
        if (body == null || body.get("choices") == null) {
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        if (choices.isEmpty()) {
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) message.get("content");

        return new ChatResponse(List.of(new Choice(0, new TextMessage("assistant", content))));
    }

    private MultiValueMap<String, Object> buildLamaRequestBody(byte[] imageBytes, byte[] maskBytes, String prompt) {
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override public String getFilename() { return "image.png"; }
        };
        ByteArrayResource maskResource = new ByteArrayResource(maskBytes) {
            @Override public String getFilename() { return "mask.png"; }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageResource);
        body.add("mask", maskResource);
        body.add("prompt", wrap(prompt));
        body.add("ldmSteps", wrap("20"));
        body.add("ldmSampler", wrap("plms"));
        body.add("hdStrategy", wrap("Original"));
        body.add("hdStrategyCropMargin", wrap("32"));
        body.add("hdStrategyCropTriggerSize", wrap("768"));
        body.add("hdStrategyResizeLimit", wrap("2048"));
        body.add("useCroper", wrap("false"));
        body.add("zitsWireframe", wrap("false"));
        body.add("negativePrompt", wrap(""));
        body.add("sdScale", wrap("1.0"));
        body.add("sdMaskBlur", wrap("0"));
        body.add("sdStrength", wrap("0.75"));
        body.add("sdSteps", wrap("20"));
        body.add("sdGuidanceScale", wrap("7.5"));
        body.add("sdSampler", wrap("plms"));
        body.add("sdSeed", wrap("-1"));
        body.add("cv2Flag", wrap("INPAINT_NS"));
        body.add("cv2Radius", wrap("3"));
        return body;
    }

    private HttpEntity<String> wrap(String value) {
        return new HttpEntity<>(value);
    }

    @SuppressWarnings("unchecked")
    private static Class<Map<String, Object>> getMapType() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }
}
