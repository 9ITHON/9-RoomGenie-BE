package team9.demo.implementation.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.mission.CleaningMission;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.CleaningMissionRepository;
import team9.demo.repository.mission.TodayMissionQueryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TodayMissionReader {

    private final TodayMissionQueryRepository todayMissionQueryRepository;
    private final CleaningMissionRepository cleaningMissionRepository;

    public CleaningMission recommendOneCleaningMission(UserId userId) {
        CleaningMission mission = cleaningMissionRepository.pickRandomMission();
        if (mission == null) {
            throw new NotFoundException(ErrorCode.MISSION_NOT_FOUND);
        }
        return mission;  // 엔티티 대신 도메인 객체 사용
    }


    public String getTodayMissionContent(UserId userId, String todayMissionId) {
        return todayMissionQueryRepository.findByUserIdAndTodayMissionId(userId, todayMissionId).getContent();
    }


    public List<TodayMissionInfo> getTodayMissions(UserId userId) {
        return todayMissionQueryRepository.findAllByUserId(userId);
    }

    public TodayMissionInfo getTodayMission(UserId userId, String todayMissionId) {
        return todayMissionQueryRepository.findByUserIdAndTodayMissionId(userId, todayMissionId);
    }
}