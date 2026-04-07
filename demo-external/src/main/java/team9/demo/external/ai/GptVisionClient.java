package team9.demo.external.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import team9.demo.dto.ChatGPTRequest;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.external.config.properties.OpenAiProperties;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.ai.analysis.Choice;
import team9.demo.model.ai.analysis.TextMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * OpenAI GPT-4 Vision 호출 전담 클라이언트.
 * - 단일 이미지 분석: 정리 가이드 생성
 * - 비교 분석(before/after): 미션 자동 검증용
 *
 * 비공개 S3 객체는 GPT가 직접 fetch할 수 없으므로 Base64 data URL로 변환해 전달한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GptVisionClient {

    private final RestTemplate restTemplate;
    private final S3ImageStore s3ImageStore;
    private final OpenAiProperties openAiProperties;

    /** 단일 이미지 분석 — 정리 가이드 텍스트 응답을 반환한다. */
    public ChatResponse requestImageAnalysis(String imageUrl, String requestText) {
        try {
            String dataUrl = "data:image/jpeg;base64," + s3ImageStore.encodeBase64(imageUrl);
            ChatGPTRequest requestDto = ChatGPTRequest.of(openAiProperties.model(), requestText, dataUrl, 500);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    openAiProperties.chatEndpoint(), HttpMethod.POST, new HttpEntity<>(requestDto), getMapType()
            );
            return parseChatResponse(response);

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("OpenAI 사용량 초과: {}", e.getResponseBodyAsString());
            throw new AiException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
        } catch (IOException e) {
            throw new AiException(ErrorCode.AI_IMAGE_READ_FAILED);
        }
    }

    /** before/after 두 이미지를 비교 분석한다 — 미션 검증용. */
    public ChatResponse requestCompareAnalysis(String beforeUrl, String afterUrl, String requestText) {
        try {
            String beforeBase64 = s3ImageStore.encodeBase64(beforeUrl);
            String afterBase64 = s3ImageStore.encodeBase64(afterUrl);

            Map<String, Object> requestBody = Map.of(
                    "model", openAiProperties.model(),
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
                    openAiProperties.chatEndpoint(), HttpMethod.POST, new HttpEntity<>(requestBody), getMapType()
            );
            return parseChatResponse(response);

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("OpenAI 사용량 초과: {}", e.getResponseBodyAsString());
            throw new AiException(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
        } catch (Exception e) {
            log.error("비교 분석 실패", e);
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }
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
        if (message == null) {
            throw new AiException(ErrorCode.AI_PROMPT_FAILED);
        }
        String content = (String) message.get("content");

        return new ChatResponse(List.of(new Choice(0, new TextMessage("assistant", content))));
    }

    @SuppressWarnings("unchecked")
    private static Class<Map<String, Object>> getMapType() {
        return (Class<Map<String, Object>>) (Class<?>) Map.class;
    }
}
