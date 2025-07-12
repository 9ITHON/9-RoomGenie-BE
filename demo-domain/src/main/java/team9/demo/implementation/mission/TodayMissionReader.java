package team9.demo.implementation.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.TodayMissionQueryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TodayMissionReader {

    private final TodayMissionQueryRepository todayMissionQueryRepository;

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