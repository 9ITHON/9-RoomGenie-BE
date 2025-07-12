package team9.demo.repository.mission;

import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;

import java.time.LocalDateTime;

public interface TodayMissionRepository {
    long countTodayMissions(UserId userId);
    void save(UserId userId, String missionId, LocalDateTime targetDate);
    void updateStatus(String todayMissionId, MissionStatus status);
}
