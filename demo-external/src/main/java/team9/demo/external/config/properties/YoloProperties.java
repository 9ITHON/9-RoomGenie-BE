package team9.demo.external.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 자체 호스팅 YOLO 감지 서버 설정.
 *
 * @param url YOLO 서버 base URL (application.properties: ai.yolo.url)
 */
@ConfigurationProperties(prefix = "ai.yolo")
public record YoloProperties(
        @DefaultValue("http://localhost:5000") String url
) {
}
