package team9.demo.dto.response.mission;


import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.mission.CleaningMission;

public record MissionRecommendResponse(String missionId, String content) {
    public static MissionRecommendResponse from(CleaningMission m) {
        return new MissionRecommendResponse(m.getMissionId(), m.getContent());
    }
}
