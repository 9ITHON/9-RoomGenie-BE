package team9.demo.repository.jpa.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.mission.CleaningMissionJpaEntity;
import team9.demo.jparepository.mission.CleaningMissionJpaRepository;
import team9.demo.model.mission.CleaningMission;
import team9.demo.repository.mission.CleaningMissionRepository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class CleaningMissionRepositoryImpl implements CleaningMissionRepository {

    private final CleaningMissionJpaRepository cleaningMissionJpaRepository;

    @Override
    public String save(String content) {
        CleaningMissionJpaEntity mission = CleaningMissionJpaEntity.generate(LocalDateTime.now(), content);
        cleaningMissionJpaRepository.save(mission); // 굳이 해야하나? 불필요해보인다.
        return mission.getMissionId();
    }


    @Override
    public CleaningMission pickRandomMission() {
        CleaningMissionJpaEntity entity = cleaningMissionJpaRepository.findOneRandom();
        if (entity == null) {
            return null;
        }
        return new CleaningMission(entity.getMissionId(), entity.getContent());
    }



}
