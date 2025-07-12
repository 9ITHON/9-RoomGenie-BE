package team9.demo.external;



import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.model.ai.analysis.*;
import team9.demo.model.ai.generate.ImageGenerationRequest;
import team9.demo.model.ai.generate.ImageGenerationResponse;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.user.UserId;
import team9.demo.repository.ai.AiRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AiRepositoryImpl implements AiRepository {

    private final WebClient.Builder webClientBuilder;

    // ========= 🔧 설정 값 ==========
    @Value("${openai.api.url.image}")
    private String editsEndpoint;

    @Value("${openai.api.url2}")
    private String Endpoint;

    @Value("${openai.api.url}")
    private String visionEndpoint;

    @Value("${openai.model1}")
    private String model;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    // ========= 🔗 의존성 ==========
    private final RestTemplate restTemplate;
    private final AmazonS3 amazonS3;

    // =========================================================================
    // ✅ [1] GPT Vision 단일 이미지 분석 (base64 변환 + 요청)
    // =========================================================================
    @Override
    public ChatResponse requestImageAnalysis(String imageUrl, String requestText) {
        try {
            System.out.println("Downloading image from S3 URL: {}" +  imageUrl);
            byte[] imageBytes = downloadImageFromS3(imageUrl);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String dataUrl = "data:image/jpeg;base64," + base64;

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text", "text", requestText),
                                    Map.of("type", "image_url", "image_url", Map.of("url", dataUrl))
                            )
                    )),
                    "max_tokens", 500
            );
            System.out.println("GPT 분석 요청: {}" + requestBody);
            ResponseEntity<Map> response = restTemplate.postForEntity(Endpoint, requestBody, Map.class);
            return parseChatResponse(response);

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("❗ OpenAI 사용량 초과: {}", e.getResponseBodyAsString());
            throw new AiException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
        } catch (IOException e) {
            throw new IllegalStateException("S3 이미지 읽기 실패", e);
        } catch (Exception e) {
            log.error("GPT 분석 실패", e);
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }
    }

    // =========================================================================
    // ✅ [2] GPT Vision 비교 분석 (Before/After 이미지 비교)
    // =========================================================================
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

            ResponseEntity<Map> response = restTemplate.postForEntity(Endpoint, requestBody, Map.class);
            return parseChatResponse(response);

        } catch (Exception e) {
            System.out.println("분석 실패: " + e);
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }
    }

    // =========================================================================
    // ✅ [3] GPT Vision - 어질러진 영역 텍스트 분석 (마스크용 좌표 등 추출용)
    // =========================================================================

    @Override
    public List<Box> detectClutterBoxes(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> request = new HttpEntity<>(imageBytes, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                "http://localhost:5000/yolo",  // YOLO Flask 서버 주소
                HttpMethod.POST,
                request,
                List.class
        );

        // 응답 파싱
        List<Map<String, Object>> rawBoxes = (List<Map<String, Object>>) response.getBody();
        return rawBoxes.stream()
                .map(map -> new Box(
                        (int) map.get("x"),
                        (int) map.get("y"),
                        (int) map.get("width"),
                        (int) map.get("height")
                ))
                .toList();
    }
    // [4] nn
    @Override
    public String editImageWithLama(byte[] imageBytes, byte[] maskBytes, UserId userId) {
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override public String getFilename() { return "image.png"; }
        };
        ByteArrayResource maskResource = new ByteArrayResource(maskBytes) {
            @Override public String getFilename() { return "mask.png"; }
        };

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("image", imageResource);
        requestMap.add("mask", maskResource);

        // 🎯 lama-cleaner 필수 form 필드들
        requestMap.add("prompt", wrap("Remove all cups, bags, and clutter from the desk surface. Keep the wall and bookshelf unchanged. Make the desk look clean and empty with a neat white surface.\n"));
        requestMap.add("ldmSteps", wrap("20"));
        requestMap.add("ldmSampler", wrap("plms"));
        requestMap.add("hdStrategy", wrap("Original"));
        requestMap.add("hdStrategyCropMargin", wrap("32"));
        requestMap.add("hdStrategyCropTriggerSize", wrap("768"));
        requestMap.add("hdStrategyResizeLimit", wrap("2048"));
        requestMap.add("useCroper", wrap("false"));
        requestMap.add("zitsWireframe", wrap("false"));
        requestMap.add("negativePrompt", wrap(""));
        requestMap.add("croperX", wrap("0"));
        requestMap.add("croperY", wrap("0"));
        requestMap.add("croperWidth", wrap("0"));
        requestMap.add("croperHeight", wrap("0"));
        requestMap.add("sdScale", wrap("1.0"));
        requestMap.add("sdMaskBlur", wrap("0"));
        requestMap.add("sdStrength", wrap("0.75"));
        requestMap.add("sdSteps", wrap("20"));
        requestMap.add("sdGuidanceScale", wrap("7.5"));
        requestMap.add("sdSampler", wrap("plms"));
        requestMap.add("sdSeed", wrap("-1"));
        requestMap.add("sdMatchHistograms", wrap("false"));
        requestMap.add("cv2Flag", wrap("INPAINT_NS"));
        requestMap.add("cv2Radius", wrap("3"));
        requestMap.add("paintByExampleSteps", wrap("20"));
        requestMap.add("paintByExampleGuidanceScale", wrap("7.5"));
        requestMap.add("paintByExampleMaskBlur", wrap("0"));
        requestMap.add("paintByExampleSeed", wrap("-1"));
        requestMap.add("paintByExampleMatchHistograms", wrap("false"));
        requestMap.add("p2pSteps", wrap("0"));
        requestMap.add("p2pImageGuidanceScale", wrap("0"));
        requestMap.add("p2pGuidanceScale", wrap("0"));
        requestMap.add("controlnet_conditioning_scale", wrap("1.0"));
        requestMap.add("controlnet_method", wrap("none"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(List.of(
                new FormHttpMessageConverter(),
                new ByteArrayHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()
        ));

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "http://localhost:7870/inpaint",
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        byte[] cleanedImage = response.getBody();
        return uploadToS3(cleanedImage, userId);
    }

    /**
     * Helper method to wrap simple string into HttpEntity
     */
    private HttpEntity<String> wrap(String value) {
        return new HttpEntity<>(value);
    }




    public String uploadToS3(byte[] image, UserId userId) {
        String key = "AI/cleaned/" + userId.getId() + "/" + UUID.randomUUID() + ".png";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType("image/png");

        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(bucket, key, inputStream, metadata);
        } catch (IOException e) {
            log.error("S3 업로드 실패: {}", e.getMessage(), e);
            throw new AiException(ErrorCode.AI_IMAGE_GENERATED_FAILED);
        }

        return amazonS3.getUrl(bucket, key).toString();
    }





    // =========================================================================
    // ✅ 헬퍼 메서드
    // =========================================================================
    private byte[] downloadImageFromS3(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        String host = url.getHost();  // e.g. roomgenie-bucket.s3.ap-northeast-2.amazonaws.com
        String path = url.getPath();  // e.g. /AI/cleaned/xxx.png

        String resolvedBucket;
        String key;

        // 1) Path style URL: https://s3.region.amazonaws.com/bucket/key
        // ex) path = /bucket/AI/xxx.png
        if (path.startsWith("/" + bucket + "/")) {
            resolvedBucket = bucket;
            key = path.substring(("/" + bucket + "/").length());
        }
        // 2) Virtual-hosted style URL: https://bucket.s3.region.amazonaws.com/key
        // ex) host = bucket.s3.region.amazonaws.com, path = /AI/xxx.png
        else if (host.startsWith(bucket + ".")) {
            resolvedBucket = bucket;
            if (path.startsWith("/")) path = path.substring(1); // remove leading slash
            key = path;
        } else {
            throw new IllegalArgumentException("Invalid S3 URL format.");
        }

        log.debug("S3 다운로드 버킷: {}, 키: {}", resolvedBucket, key);

        S3Object object = amazonS3.getObject(resolvedBucket, key);
        try (InputStream inputStream = object.getObjectContent()) {
            return inputStream.readAllBytes();
        }
    }

    private String encodeImageFromS3(String imageUrl) throws IOException {
        return Base64.getEncoder().encodeToString(downloadImageFromS3(imageUrl));
    }

    public InputStream downloadImageForGenerate(String s3Url) {
        try {
            URI uri = new URI(s3Url);
            String host = uri.getHost(); // ex) roomgenie-bucket.s3.ap-northeast-2.amazonaws.com
            String bucket = null;
            String key = null;

            // 호스트에서 첫 번째 점(.) 이전 부분을 버킷명으로 추출
            if (host != null && host.endsWith("amazonaws.com")) {
                bucket = host.substring(0, host.indexOf('.'));
            } else {
                throw new IllegalArgumentException("지원하지 않는 S3 URL 호스트 형식입니다: " + host);
            }

            // 경로에서 첫 글자 '/' 제거 후 키로 사용
            String path = uri.getPath(); // ex) /AI/cleaned/...
            if (path != null && path.length() > 1) {
                key = path.substring(1);
            } else {
                throw new IllegalArgumentException("잘못된 S3 URL 경로입니다: " + path);
            }

            // S3 SDK로 객체 가져오기
            S3Object s3Object = amazonS3.getObject(bucket, key);
            return s3Object.getObjectContent();

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + s3Url, e);
        }
    }

    private ChatResponse parseChatResponse(ResponseEntity<Map> response) {
        if (response.getBody() == null || response.getBody().get("choices") == null) {
            log.error("OpenAI 응답이 null이거나 'choices' 없음");
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }

        List<?> choices = (List<?>) response.getBody().get("choices");
        if (choices.isEmpty()) {
            log.error("OpenAI 응답 choices가 비어있음");
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }

        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        String content = (String) message.get("content");

        return new ChatResponse(List.of(new Choice(0, new TextMessage("assistant", content))));
    }
}
