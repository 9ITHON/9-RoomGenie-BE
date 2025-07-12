package team9.demo.jparepository.mission;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.mission.CleaningMissionJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CleaningMissionJpaRepository extends JpaRepository<CleaningMissionJpaEntity, String> {

}
