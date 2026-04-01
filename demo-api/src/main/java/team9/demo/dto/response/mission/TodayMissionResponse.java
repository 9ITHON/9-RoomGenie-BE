package team9.demo.dto.response.mission;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.mission.TodayMissionInfo;

import java.time.LocalDateTime;

public record TodayMissionResponse(
        String missionId,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime targetDate,
        MissionStatus status
) {
    /** 기존: from(TodayMissionInfo) */
    public static TodayMissionResponse from(TodayMissionInfo info) {
        return new TodayMissionResponse(
                info.getMissionId(),
                info.getContent(),
                info.getTargetDate(),
                info.getStatus()
        );
    }

    /** 기존 Lombok @AllArgsConstructor(staticName="from") 대체 */
    public static TodayMissionResponse from(String missionId, String content,
                                            LocalDateTime targetDate, MissionStatus status) {
        return new TodayMissionResponse(missionId, content, targetDate, status);
    }
}

