package team9.demo.jparepository.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team9.demo.jpaentity.mission.TodayMissionJpaEntity;
import team9.demo.model.user.UserId;

import java.time.LocalDateTime;
import java.util.List;

public interface TodayMissionJpaRepository extends JpaRepository<TodayMissionJpaEntity, String> {
    long countByUserIdAndCreatedAtBetween(String userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<TodayMissionJpaEntity> findAllByUserId(String userId);

    @Query("SELECT t, c FROM TodayMissionJpaEntity t JOIN CleaningMissionJpaEntity c ON t.missionId = c.missionId WHERE t.userId = :userId")
    List<Object[]> findAllWithMissionByUserId(@Param("userId") String userId);

    @Query("SELECT t, c FROM TodayMissionJpaEntity t JOIN CleaningMissionJpaEntity c ON t.missionId = c.missionId WHERE t.todayMissionId = :todayMissionId AND t.userId = :userId")
    List<Object[]> findWithMissionByIdAndUserId(@Param("todayMissionId") String todayMissionId, @Param("userId") String userId);
}
