package team9.demo.repository.jpa.mission;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.mission.CleaningMissionJpaEntity;
import team9.demo.jparepository.mission.CleaningMissionJpaRepository;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.MissionTemplateRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Repository
@RequiredArgsConstructor
public class MissionTemplateRepositoryImpl implements MissionTemplateRepository {

    private final CleaningMissionJpaRepository cleaningMissionJpaRepository;

    @Override
    public String pickRandomMission() {
        List<CleaningMissionJpaEntity> all = cleaningMissionJpaRepository.findAll();

        if (all.isEmpty()) {
            return null; // 또는 throw new NotFoundException(...)
        }

        int idx = ThreadLocalRandom.current().nextInt(all.size());
        return all.get(idx).getContent();
    }

}
