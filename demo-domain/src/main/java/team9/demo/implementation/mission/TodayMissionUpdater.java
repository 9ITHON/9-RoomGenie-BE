package team9.demo.implementation.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.CleaningMissionRepository;
import team9.demo.repository.mission.MissionTemplateRepository;
import team9.demo.repository.mission.TodayMissionRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TodayMissionUpdater {

    private final TodayMissionRepository todayMissionRepository;
    private final MissionTemplateRepository missionTemplateRepository;
    private final CleaningMissionRepository cleaningMissionRepository;


    public void updateStatus(String todayMissionId, MissionStatus status) {
        todayMissionRepository.updateStatus(todayMissionId, status);
    }

    public String recommendOneCleaningMission(UserId userId) {

        if(missionTemplateRepository.pickRandomMission() == null){
            throw new NotFoundException(ErrorCode.MISSION_NOT_FOUND);
        };
        return missionTemplateRepository.pickRandomMission();
    }

    public void makeTodayMission(UserId userId, String content) {
        long count = todayMissionRepository.countTodayMissions(userId);
        if (count >= 3) {
            throw new ConflictException(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED);
        }

        String missionId = cleaningMissionRepository.save(content);
        todayMissionRepository.save(userId, missionId, LocalDateTime.now().plusDays(1));
    }

    public void acceptRecommendedMission(UserId userId, String missionId) {
        long count = todayMissionRepository.countTodayMissions(userId);
        if (count >= 3) {
            throw new ConflictException(ErrorCode.TODAY_MISSION_LIMIT_EXCEEDED);
        }

        todayMissionRepository.save(userId, missionId, LocalDateTime.now().plusDays(1));
    }
}

