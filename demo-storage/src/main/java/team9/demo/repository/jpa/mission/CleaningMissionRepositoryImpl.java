package team9.demo.repository.jpa.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.mission.CleaningMissionJpaEntity;
import team9.demo.jparepository.mission.CleaningMissionJpaRepository;
import team9.demo.repository.mission.CleaningMissionRepository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class CleaningMissionRepositoryImpl implements CleaningMissionRepository {

    private final CleaningMissionJpaRepository cleaningMissionJpaRepository;

    @Override
    public String save(String content) {
        CleaningMissionJpaEntity mission = CleaningMissionJpaEntity.generate(LocalDateTime.now(), content);
        cleaningMissionJpaRepository.save(mission);
        return mission.getMissionId();
    }
}
