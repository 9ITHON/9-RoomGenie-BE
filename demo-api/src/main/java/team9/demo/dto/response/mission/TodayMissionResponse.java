package team9.demo.dto.response.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.mission.TodayMissionInfo;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "from")
public class TodayMissionResponse {
    private String missionId;
    private String content;
    private LocalDateTime targetDate;
    private MissionStatus status;

    public static TodayMissionResponse from(TodayMissionInfo info) {
        return new TodayMissionResponse(
                info.getMissionId(),
                info.getContent(),
                info.getTargetDate(),
                info.getStatus()
        );
    }
}

