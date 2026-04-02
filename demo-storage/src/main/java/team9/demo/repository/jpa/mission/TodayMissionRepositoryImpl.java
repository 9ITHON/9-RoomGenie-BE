package team9.demo.repository.jpa.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.jpaentity.mission.TodayMissionJpaEntity;
import team9.demo.jparepository.mission.TodayMissionJpaRepository;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.TodayMissionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class TodayMissionRepositoryImpl implements TodayMissionRepository {

    private final TodayMissionJpaRepository todayMissionJpaRepository;

    @Override
    public long countTodayMissions(UserId userId) {
        LocalDate today = LocalDate.now(); // 오늘 날짜
        LocalDateTime startOfDay = today.atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999); // 23:59:59.999999999

        return todayMissionJpaRepository.countByUserIdAndCreatedAtBetween(
                userId.getId(), startOfDay, endOfDay
        );
    }

    @Override
    public void save(UserId userId, String missionId, LocalDateTime targetDate) {
        TodayMissionJpaEntity entity = TodayMissionJpaEntity.generate(targetDate, userId.getId(), missionId);
        todayMissionJpaRepository.save(entity);
    }
    @Override
    public void updateStatus(String todayMissionId, MissionStatus status) {
        TodayMissionJpaEntity entity = todayMissionJpaRepository.findById(todayMissionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MISSION_NOT_FOUND));
        entity.updateStatus(status);
        todayMissionJpaRepository.save(entity);
    }

    @Override
    public String findUserIdByMissionId(String todayMissionId) {
        return todayMissionJpaRepository.findById(todayMissionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MISSION_NOT_FOUND))
                .getUserId();
    }
}
