package team9.demo.facade.ai;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.implementation.ai.AiAppender;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;
import team9.demo.model.user.UserId;
import team9.demo.service.user.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AiFacade {

    private final UserService userService;
    private final AiAppender aiAppender;

    public ChatResponse requestImageAnalysis(FileData file, String requestText, UserId userId) {
        Media media = userService.uploadFile(file, userId, FileCategory.USER);
        return aiAppender.requestImageAnalysis(media.getUrl(), requestText, userId);

    }


    // 텍스트 분석
    public String requestImageAnalysisText(String imageUrl, UserId userId) {
        ChatResponse analysisResponse = aiAppender.requestImageAnalysis(imageUrl, "해당 이미지를 보고 분석 후 어지럽혀진 방을 깔끔하게 정리할 수 있도록 가이드를 작성해 주세요.\n"
                , userId);
        return analysisResponse.getResultMessage();
    }

    // ✅ Service
    public String generateCleanedRoomImageWithLama(FileData file, UserId userId) {
        Media original = userService.uploadFile(file, userId, FileCategory.AI);

        BufferedImage bufferedImage;
        try (InputStream is = new ByteArrayInputStream(file.getContent())) {
            bufferedImage = ImageIO.read(is);
        } catch (IOException e) {
            throw new AiException(ErrorCode.AI_IMAGE_GENERATED_FAILED);
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        return aiAppender.cleanImageWithLama(file, width, height, userId);
    }





}
