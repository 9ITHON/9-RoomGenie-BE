package team9.demo.model.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class TodayMissionInfo {
    private String missionId;
    private String content;
    private LocalDateTime targetDate;
    private MissionStatus status;
}