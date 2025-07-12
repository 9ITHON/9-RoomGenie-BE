package team9.demo.implementation.ai;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.model.ai.analysis.ChatResponse;
//import team9.demo.service.user.ResultAppender;
import team9.demo.model.ai.mask.Box;
import team9.demo.model.ai.mask.BoxParser;
import team9.demo.model.ai.mask.MaskGenerator;
import team9.demo.model.media.FileData;
import team9.demo.model.user.UserId;
import team9.demo.repository.ai.AiRepository;
import team9.demo.service.user.ResultAppender;


import java.util.List;

@Component
@RequiredArgsConstructor
public class AiAppender {


    private final AiRepository aiRepository;
    private final ResultAppender resultAppender;

    public ChatResponse requestImageAnalysis(String imageUrl, String requestText, UserId userId) {
        // 저장은 인터페이스 통해 위임
        ChatResponse response = aiRepository.requestImageAnalysis(imageUrl, requestText);
        resultAppender.saveAnalysisResult(userId, response.getResultMessage(), imageUrl); // 저장 위임
        return response;
    }

    public ChatResponse requestComparisonAnalysis(String beforeUrl, String afterUrl, String requestText) {
        return aiRepository.requestCompareAnalysis(beforeUrl, afterUrl, requestText);
    }

    public String cleanImageWithLama(FileData originalImage, int width, int height, UserId userId) {
        try {
            List<Box> clutterBoxes = aiRepository.detectClutterBoxes(originalImage.getContent());

            if (clutterBoxes.isEmpty()) throw new IllegalArgumentException("YOLO returned no boxes");

            byte[] maskImage = MaskGenerator.createMask(clutterBoxes, width, height);

            return aiRepository.editImageWithLama(originalImage.getContent(), maskImage, userId);
        } catch (Exception e) {
            System.out.println("YOLO or LamaCleaner failed" + e.getMessage());
            throw new AiException(ErrorCode.AI_IMAGE_GENERATED_FAILED);
        }
    }




}
