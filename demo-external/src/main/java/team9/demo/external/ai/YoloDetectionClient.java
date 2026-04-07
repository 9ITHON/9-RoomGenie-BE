package team9.demo.external.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.external.config.properties.YoloProperties;
import team9.demo.model.ai.mask.Box;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 자체 호스팅 YOLO 서버 호출 전담 클라이언트.
 * 방 사진에서 어질러진 객체의 bounding box를 감지하여 반환한다.
 * 반환된 box들은 MaskGenerator에서 LAMA 인페인팅용 마스크로 후처리된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class YoloDetectionClient {

    private final RestTemplate restTemplate;
    private final YoloProperties yoloProperties;

    public List<Box> detectClutterBoxes(byte[] imageBytes) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            ResponseEntity<List> response = restTemplate.exchange(
                    yoloProperties.url() + "/yolo",
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
        } catch (Exception e) {
            log.error("YOLO 감지 실패: {}", e.getMessage(), e);
            throw new AiException(ErrorCode.AI_DETECTION_FAILED);
        }
    }
}
