package team9.demo.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.external.ai.GptVisionClient;
import team9.demo.external.ai.LamaInpaintClient;
import team9.demo.external.ai.YoloDetectionClient;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.user.UserId;
import team9.demo.repository.ai.ExternalAiClient;

import java.util.List;

/**
 * 도메인 계층의 단일 진입점 역할을 하는 thin composite.
 * 실제 호출은 책임이 분리된 3개 클라이언트에 위임한다:
 *   - GPT-4 Vision (분석/비교)  → {@link GptVisionClient}
 *   - YOLO (어지러운 영역 감지) → {@link YoloDetectionClient}
 *   - LAMA (인페인팅)            → {@link LamaInpaintClient}
 *
 * 도메인 계층은 본 인터페이스에만 의존하므로 내부 구현 분리/교체에 영향을 받지 않는다.
 */
@Component
@RequiredArgsConstructor
public class ExternalAiClientImpl implements ExternalAiClient {

    private final GptVisionClient gptVisionClient;
    private final YoloDetectionClient yoloDetectionClient;
    private final LamaInpaintClient lamaInpaintClient;

    @Override
    public ChatResponse requestImageAnalysis(String imageUrl, String requestText) {
        return gptVisionClient.requestImageAnalysis(imageUrl, requestText);
    }

    @Override
    public ChatResponse requestCompareAnalysis(String beforeUrl, String afterUrl, String requestText) {
        return gptVisionClient.requestCompareAnalysis(beforeUrl, afterUrl, requestText);
    }

    @Override
    public List<Box> detectClutterBoxes(byte[] imageBytes) {
        return yoloDetectionClient.detectClutterBoxes(imageBytes);
    }

    @Override
    public String editImageWithLama(byte[] imageBytes, byte[] maskBytes, String prompt, UserId userId) {
        return lamaInpaintClient.editImage(imageBytes, maskBytes, prompt, userId);
    }
}
