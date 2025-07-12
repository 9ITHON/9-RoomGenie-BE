package team9.demo.jparepository.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.mission.TodayMissionJpaEntity;
import team9.demo.model.user.UserId;

import java.time.LocalDateTime;
import java.util.List;

public interface TodayMissionJpaRepository extends JpaRepository<TodayMissionJpaEntity, String> {
    long countByUserIdAndCreatedAtBetween(String userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<TodayMissionJpaEntity> findAllByUserId(String userId);
}
