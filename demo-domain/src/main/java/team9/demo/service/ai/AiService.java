package team9.demo.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team9.demo.error.AiException;
import team9.demo.error.ErrorCode;
import team9.demo.implementation.ai.AiProcessor;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiProcessor aiAppender;
    private final UserService userService;
    private final TodayMissionUpdater todayMissionUpdater;
    private final TodayMissionReader todayMissionReader;





    public String verifyTodayMissionByImageWithContext(String todayMissionId, FileData before, FileData after, UserId userId) {
        // 1. 미션 내용 조회
        String missionContent = todayMissionReader.getTodayMissionContent(userId, todayMissionId);

        // 2. 이미지 업로드
        Media beforeMedia = userService.uploadFile(before, userId, FileCategory.USER);
        Media afterMedia = userService.uploadFile(after, userId, FileCategory.USER);

        // 3. AI 프롬프트 생성 - 구조화된 응답을 요청
        String prompt = "오늘의 미션은 다음과 같습니다: [" + missionContent + "]. " +
                "아래의 두 이미지를 비교해 이 미션이 성공적으로 수행되었는지 평가해주세요. " +
                "방이 깨끗한 것과 미션 성공 여부는 다르니 미션에 집중해주세요. " +
                "반드시 응답의 첫 줄에 [RESULT:SUCCESS] 또는 [RESULT:FAIL]을 포함해주세요. " +
                "그 다음 줄부터 평가 내용을 작성해주세요.";

        // 4. 분석 요청
        ChatResponse response = aiAppender.requestComparisonAnalysis(beforeMedia.getUrl(), afterMedia.getUrl(), prompt);
        String message = response.getResultMessage();

        // 5. 상태 판단 및 반영 - 구조화된 태그 기반 판단
        boolean isComplete = message.contains("[RESULT:SUCCESS]");

        log.info("미션 완료 여부: {}, todayMissionId: {}", isComplete, todayMissionId);
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