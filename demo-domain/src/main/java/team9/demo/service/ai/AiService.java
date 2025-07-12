package team9.demo.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.implementation.ai.AiAppender;
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
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiAppender aiAppender;
    private final UserService userService;
    private final TodayMissionUpdater todayMissionUpdater;
    private final TodayMissionReader todayMissionReader;

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



    public String verifyTodayMissionByImageWithContext(String todayMissionId, FileData before, FileData after, UserId userId) {
        // 1. 미션 내용 조회
        String missionContent = todayMissionReader.getTodayMissionContent(userId, todayMissionId);

        // 2. 이미지 업로드
        Media beforeMedia = userService.uploadFile(before, userId, FileCategory.USER);
        Media afterMedia = userService.uploadFile(after, userId, FileCategory.USER);

        // 3. AI 프롬프트 생성
        String prompt = "오늘의 미션은 다음과 같습니다: [" + missionContent + "]. 아래의 두 이미지를 비교해 이 미션이 성공적으로 수행되었는지 평가해주세요." +
                "방이 깨끗한거랑 미션성공여부랑 다르니 미션에 집중해주세요";

        // 4. 분석 요청
        ChatResponse response = aiAppender.requestComparisonAnalysis(beforeMedia.getUrl(), afterMedia.getUrl(), prompt);
        String message = response.getResultMessage();

        // 5. 상태 판단 및 반영
        String lower = message.toLowerCase();

        boolean isComplete = (
                lower.contains("완료") ||
                        lower.contains("성공") ||
                        lower.contains("정리되었습니다") ||
                        lower.contains("정리가 잘 되어")
        ) &&
                !lower.contains("판단하기 어렵") &&
                !lower.contains("알 수 없") &&
                !lower.contains("불분명");

        System.out.println("완료 여부 = " + isComplete);
        MissionStatus resultStatus = isComplete ? MissionStatus.COMPLETED : MissionStatus.ONGOING;
        todayMissionUpdater.updateStatus(todayMissionId, resultStatus);

        // 6. 성공/실패 메시지 반환
        if (isComplete) {
            return "성공입니다, " + message;
        } else {
            return "실패입니다, " + message;
        }
    }


}