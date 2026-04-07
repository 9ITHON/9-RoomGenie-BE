package team9.demo.external.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.external.config.properties.OpenAiProperties;
import team9.demo.model.ai.analysis.ChatResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * {@link GptVisionClient}의 OpenAI 호출 동작 검증.
 * <p>
 * 이 테스트는 PR2(@Value → @ConfigurationProperties)의 효과를 직접 보여준다 —
 * Spring 컨텍스트 없이 record properties 인스턴스를 그냥 new 해서 주입하면 끝.
 * <p>
 * RestTemplate은 Spring Boot 내장 {@link MockRestServiceServer}로 모킹하여
 * 실제 OpenAI 호출 없이 응답 시나리오별 동작을 검증한다.
 */
class GptVisionClientTest {

    private static final String CHAT_ENDPOINT = "https://api.openai.test/v1/chat/completions";
    private static final String IMAGE_URL = "https://bucket.s3.amazonaws.com/test.jpg";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private S3ImageStore s3ImageStore;
    private GptVisionClient client;

    @BeforeEach
    void setUp() throws IOException {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        // S3 다운로드 부분만 모킹 — 실제 S3 호출 제거
        s3ImageStore = mock(S3ImageStore.class);
        when(s3ImageStore.encodeBase64(anyString())).thenReturn("BASE64_DUMMY");

        // ⭐ PR2의 효과: Spring 컨텍스트 없이 record 인스턴스만으로 주입 가능
        OpenAiProperties properties = new OpenAiProperties("test-key", CHAT_ENDPOINT, "gpt-4-vision-preview");

        client = new GptVisionClient(restTemplate, s3ImageStore, properties);
    }

    @Test
    @DisplayName("정상 응답: choices[0].message.content를 ChatResponse로 파싱한다")
    void requestImageAnalysis_success() {
        String body = """
                {
                  "choices": [
                    {
                      "message": {
                        "role": "assistant",
                        "content": "방을 정리하려면 책상 위 물건부터 분류하세요."
                      }
                    }
                  ]
                }
                """;
        mockServer.expect(requestTo(CHAT_ENDPOINT))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        ChatResponse response = client.requestImageAnalysis(IMAGE_URL, "정리법 알려줘");

        assertThat(response).isNotNull();
        assertThat(response.getResultMessage()).contains("책상 위 물건부터 분류");
        mockServer.verify();
    }

    @Test
    @DisplayName("OpenAI 429 응답은 AI_RATE_LIMIT_EXCEEDED 도메인 예외로 래핑된다")
    void requestImageAnalysis_tooManyRequests_wrappedToRateLimitError() {
        mockServer.expect(requestTo(CHAT_ENDPOINT))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .body("{\"error\":{\"message\":\"rate limit\"}}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.requestImageAnalysis(IMAGE_URL, "test"))
                .isInstanceOf(AiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("choices가 비어있는 응답은 AI_PROMPT_FAILED로 처리된다")
    void requestImageAnalysis_emptyChoices_throwsPromptFailed() {
        mockServer.expect(requestTo(CHAT_ENDPOINT))
                .andRespond(withSuccess("{\"choices\": []}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.requestImageAnalysis(IMAGE_URL, "test"))
                .isInstanceOf(AiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AI_PROMPT_FAILED);
    }

    @Test
    @DisplayName("비교 분석(before/after)도 정상 응답을 ChatResponse로 파싱한다")
    void requestCompareAnalysis_success() throws IOException {
        when(s3ImageStore.encodeBase64("before.jpg")).thenReturn("BEFORE_B64");
        when(s3ImageStore.encodeBase64("after.jpg")).thenReturn("AFTER_B64");

        mockServer.expect(requestTo(CHAT_ENDPOINT))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            { "message": { "role": "assistant", "content": "[RESULT:SUCCESS]" } }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        ChatResponse response = client.requestCompareAnalysis("before.jpg", "after.jpg", "비교해줘");

        assertThat(response.getResultMessage()).contains("[RESULT:SUCCESS]");
        mockServer.verify();
    }

    @Test
    @DisplayName("비교 분석에서 OpenAI 429도 AI_RATE_LIMIT_EXCEEDED로 일관 처리된다 (PR1 사일런트 버그 수정 검증)")
    void requestCompareAnalysis_tooManyRequests_wrappedToRateLimitError() throws IOException {
        when(s3ImageStore.encodeBase64(anyString())).thenReturn("B64");

        mockServer.expect(requestTo(CHAT_ENDPOINT))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .body("{}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.requestCompareAnalysis("a.jpg", "b.jpg", "test"))
                .isInstanceOf(AiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AI_RATE_LIMIT_EXCEEDED);
    }
}
