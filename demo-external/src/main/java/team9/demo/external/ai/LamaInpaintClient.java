package team9.demo.external.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.external.config.properties.LamaProperties;
import team9.demo.model.user.UserId;

import java.util.List;

/**
 * 자체 호스팅 LAMA 서버를 호출해 마스크 영역을 인페인팅한다.
 * <p>
 * 기본 RestTemplate은 multipart/form-data + binary mask + 다수 form 파라미터 동시 전송을
 * 처리하지 못하므로, 본 클래스는 FormHttpMessageConverter / ByteArrayHttpMessageConverter /
 * MappingJackson2HttpMessageConverter 3종을 등록한 전용 RestTemplate 인스턴스를 사용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LamaInpaintClient {

    private final S3ImageStore s3ImageStore;
    private final LamaProperties lamaProperties;

    private final RestTemplate lamaClient = new RestTemplate(List.of(
            new FormHttpMessageConverter(),
            new ByteArrayHttpMessageConverter(),
            new MappingJackson2HttpMessageConverter()
    ));

    /**
     * 원본 이미지와 마스크를 LAMA 서버에 전송해 인페인팅을 수행하고,
     * 결과 PNG를 S3에 업로드한 뒤 public URL을 반환한다.
     */
    public String editImage(byte[] imageBytes, byte[] maskBytes, String prompt, UserId userId) {
        try {
            MultiValueMap<String, Object> body = buildLamaRequestBody(imageBytes, maskBytes, prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ResponseEntity<byte[]> response = lamaClient.exchange(
                    lamaProperties.url() + "/inpaint",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    byte[].class
            );

            byte[] responseBody = response.getBody();
            if (responseBody == null || responseBody.length == 0) {
                throw new AiException(ErrorCode.AI_IMAGE_LAMA_FAILED);
            }

            return s3ImageStore.uploadCleanedImage(responseBody, userId);
        } catch (AiException e) {
            throw e;
        } catch (Exception e) {
            log.error("LAMA inpainting 실패: {}", e.getMessage(), e);
            throw new AiException(ErrorCode.AI_IMAGE_LAMA_FAILED);
        }
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
}
