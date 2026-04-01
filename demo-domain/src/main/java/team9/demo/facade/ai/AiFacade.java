package team9.demo.facade.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.implementation.ai.AiProcessor;
import team9.demo.implementation.ai.AiPromptGenerator;
import team9.demo.implementation.mission.TodayMissionReader;
import team9.demo.implementation.mission.TodayMissionUpdater;
import team9.demo.model.ai.analysis.ChatResponse;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;
import team9.demo.service.user.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFacade {

    private final UserService userService;
    private final AiProcessor aiProcessor;
    private final AiPromptGenerator aiPromptGenerator;
    private final TodayMissionReader todayMissionReader;
    private final TodayMissionUpdater todayMissionUpdater;

    public ChatResponse requestImageAnalysis(FileData file, String requestText, UserId userId) {
        Media media = userService.uploadFile(file, userId, FileCategory.USER);
        return aiProcessor.requestImageAnalysis(media.getUrl(), requestText, userId);
    }

    public String requestImageAnalysisText(String imageUrl, UserId userId) {
        String prompt = aiPromptGenerator.roomCleaningGuide();
        ChatResponse response = aiProcessor.requestImageAnalysis(imageUrl, prompt, userId);
        return response.getResultMessage();
    }

    public String generateCleanedRoomImageWithLama(FileData file, UserId userId) {
        userService.uploadFile(file, userId, FileCategory.AI);

        try (var is = new ByteArrayInputStream(file.getContent())) {
            BufferedImage bufferedImage = ImageIO.read(is);
            if (bufferedImage == null) {
                throw new AiException(ErrorCode.AI_IMAGE_GENERATED_FAILED);
            }
            return aiProcessor.cleanImageWithLama(file, bufferedImage.getWidth(), bufferedImage.getHeight(), userId);
        } catch (IOException e) {
            throw new AiException(ErrorCode.AI_IMAGE_GENERATED_FAILED);
        }
    }

    public String verifyTodayMissionByImage(String todayMissionId, FileData before, FileData after, UserId userId) {
        String missionContent = todayMissionReader.getTodayMissionContent(userId, todayMissionId);

        Media beforeMedia = userService.uploadFile(before, userId, FileCategory.USER);
        Media afterMedia = userService.uploadFile(after, userId, FileCategory.USER);

        String prompt = aiPromptGenerator.missionVerification(missionContent);
        ChatResponse response = aiProcessor.requestComparisonAnalysis(beforeMedia.getUrl(), afterMedia.getUrl(), prompt);
        String message = response.getResultMessage();

        boolean isComplete = message.contains("[RESULT:SUCCESS]");
        log.info("미션 완료 여부: {}, todayMissionId: {}", isComplete, todayMissionId);

        MissionStatus resultStatus = isComplete ? MissionStatus.COMPLETED : MissionStatus.ONGOING;
        todayMissionUpdater.updateStatus(todayMissionId, resultStatus);

        return isComplete ? "성공입니다, " + message : "실패입니다, " + message;
    }
}
