package team9.demo.service.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.implementation.mission.TodayMissionReader;
import team9.demo.implementation.mission.TodayMissionUpdater;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final TodayMissionUpdater todayMissionUpdater;
    private final TodayMissionReader todayMissionReader;

    public String recommendOneMission(UserId userId) {
        return todayMissionUpdater.recommendOneCleaningMission(userId);
    }

    public void makeCustomTodayMission(UserId userId, String missionText) {
        todayMissionUpdater.makeTodayMission(userId, missionText);
    }
    public void acceptRecommendedTodayMission(UserId userId, String missionId) {
        todayMissionUpdater.acceptRecommendedMission(userId, missionId);
    }

    public List<TodayMissionInfo> getTodayMissions(UserId userId) {
        return todayMissionReader.getTodayMissions(userId);
    }
    public TodayMissionInfo getTodayMission(UserId userId, String todayMissionId) {
        return todayMissionReader.getTodayMission(userId, todayMissionId);
    }

}
