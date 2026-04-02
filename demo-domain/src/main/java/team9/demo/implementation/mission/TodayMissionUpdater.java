package team9.demo.implementation.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.mission.CleaningMission;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.badge.BadgeRepository;
import team9.demo.repository.mission.CleaningMissionRepository;
import team9.demo.repository.mission.TodayMissionRepository;
import team9.demo.repository.user.UserPointRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TodayMissionUpdater {

    private static final long MISSION_COMPLETION_POINTS = 5L;
    private static final int DAILY_MISSION_LIMIT = 3;

    private final TodayMissionRepository todayMissionRepository;
    private final CleaningMissionRepository cleaningMissionRepository;
    private final UserPointRepository userPointRepository;
    private final BadgeRepository badgeRepository;

    @Transactional
    public void updateStatus(String todayMissionId, MissionStatus status) {
        todayMissionRepository.updateStatus(todayMissionId, status);

        if (status == MissionStatus.COMPLETED) {
            String userId = todayMissionRepository.findUserIdByMissionId(todayMissionId);
            long currentPoint = userPointRepository.increasePoint(userId, MISSION_COMPLETION_POINTS);
            grantNewBadges(userId, currentPoint);
        }
    }

    private void grantNewBadges(String userId, long currentPoint) {
        List<String> earnedBadgeIds = badgeRepository.findEarnedBadgeIds(currentPoint);
        if (earnedBadgeIds.isEmpty()) {
            return;
        }

        List<String> alreadyGranted = badgeRepository.findAlreadyGrantedBadgeIds(userId, earnedBadgeIds);
        List<String> newBadgeIds = new ArrayList<>(earnedBadgeIds);
        newBadgeIds.removeAll(alreadyGranted);

        if (!newBadgeIds.isEmpty()) {
            badgeRepository.grantBadges(userId, newBadgeIds);
        }
    }

    public void makeTodayMission(UserId userId, String content) {
        long count = todayMissionRepository.countTodayMissions(userId);
        if (count >= DAILY_MISSION_LIMIT) {
            throw new ConflictException(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED);
        }

        String missionId = cleaningMissionRepository.save(content);
        todayMissionRepository.save(userId, missionId, LocalDateTime.now().plusDays(1));
    }

    public void acceptRecommendedMission(UserId userId, String missionId) {
        long count = todayMissionRepository.countTodayMissions(userId);
        if (count >= DAILY_MISSION_LIMIT) {
            throw new ConflictException(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED);
        }

        todayMissionRepository.save(userId, missionId, LocalDateTime.now().plusDays(1));
    }
}

