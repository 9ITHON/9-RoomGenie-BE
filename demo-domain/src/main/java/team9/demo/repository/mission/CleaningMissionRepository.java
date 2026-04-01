package team9.demo.repository.mission;

import team9.demo.model.mission.CleaningMission;

public interface CleaningMissionRepository {
    String save(String content); // missionId 반환
    CleaningMission pickRandomMission(); // 미션 랜덤 돌리기
}
