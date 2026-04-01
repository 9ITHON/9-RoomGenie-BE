package team9.demo.model.mission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CleaningMission {
    private final String missionId;
    private final String content;
}