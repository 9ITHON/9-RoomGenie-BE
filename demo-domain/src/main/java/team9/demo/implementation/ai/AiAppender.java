package team9.demo.implementation.ai;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.ai.mask.MaskGenerator;
import team9.demo.model.media.FileData;
import team9.demo.model.user.UserId;
import team9.demo.repository.ai.ExternalAiClient;
import team9.demo.service.user.AiAnalysisGenerator;




import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiAppender {


    private final ExternalAiClient externalAiClient;
    private final AiAnalysisGenerator aiAnalysisGenerator;
    // promptGenerator

    public ChatResponse requestImageAnalysis(String imageUrl, String requestText, UserId userId) {
        // 저장은 인터페이스 통해 위임
        ChatResponse response = externalAiClient.requestImageAnalysis(imageUrl, requestText);
        aiAnalysisGenerator.saveAnalysisResult(userId, response.getResultMessage(), imageUrl); // 저장 위임
        return response;
    }

    public ChatResponse requestComparisonAnalysis(String beforeUrl, String afterUrl, String requestText) {
        return externalAiClient.requestCompareAnalysis(beforeUrl, afterUrl, requestText);
    }

    public String cleanImageWithLama(FileData originalImage, int width, int height, UserId userId) {
        try {
            List<Box> clutterBoxes = externalAiClient.detectClutterBoxes(originalImage.getContent());

            if (clutterBoxes.isEmpty()) throw new IllegalArgumentException("YOLO returned no boxes");

            byte[] maskImage = MaskGenerator.createMask(clutterBoxes, width, height);

            return externalAiClient.editImageWithLama(originalImage.getContent(), maskImage, userId);
        } catch (Exception e) {
            log.error("YOLO or LamaCleaner failed: {}", e.getMessage(), e);
            throw new AiException(ErrorCode.AI_IMAGE_GENERATED_FAILED);
        }
    }




}
