package team9.demo.external.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 자체 호스팅 LAMA 인페인팅 서버 설정.
 *
 * @param url LAMA 서버 base URL (application.properties: ai.lama.url)
 */
@ConfigurationProperties(prefix = "ai.lama")
public record LamaProperties(
        @DefaultValue("http://localhost:7870") String url
) {
}
