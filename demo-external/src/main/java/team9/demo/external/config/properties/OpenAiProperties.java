package team9.demo.external.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAI 호출에 필요한 설정.
 * <p>
 * Spring Boot relaxed binding으로 application.properties의 다음 키와 매핑된다:
 * <pre>
 *   openai.api-key       → apiKey
 *   openai.chat-endpoint → chatEndpoint
 *   openai.model         → model
 * </pre>
 *
 * @param apiKey       Bearer 토큰 (RestTemplate interceptor에서 사용)
 * @param chatEndpoint chat/completions 엔드포인트
 * @param model        사용할 모델 식별자 (예: gpt-4-vision-preview)
 */
@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
        String apiKey,
        String chatEndpoint,
        String model
) {
}
