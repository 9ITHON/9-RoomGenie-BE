package team9.demo.repository.mission;

import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;

import java.util.List;

public interface TodayMissionQueryRepository {
    List<TodayMissionInfo> findAllByUserId(UserId userId);
    TodayMissionInfo findByUserIdAndTodayMissionId(UserId userId, String todayMissionId);
}
