package team9.demo.repository.jpa.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.mission.CleaningMissionJpaEntity;
import team9.demo.jpaentity.mission.TodayMissionJpaEntity;
import team9.demo.jparepository.mission.TodayMissionJpaRepository;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.TodayMissionQueryRepository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodayMissionQueryRepositoryImpl implements TodayMissionQueryRepository {

    private final TodayMissionJpaRepository todayMissionJpaRepository;

    @Override
    public List<TodayMissionInfo> findAllByUserId(UserId userId) {
        List<Object[]> results = todayMissionJpaRepository.findAllWithMissionByUserId(userId.getId());

        return results.stream()
                .map(row -> {
                    TodayMissionJpaEntity todayMission = (TodayMissionJpaEntity) row[0];
                    CleaningMissionJpaEntity mission = (CleaningMissionJpaEntity) row[1];
                    return TodayMissionInfo.of(
                            todayMission.getMissionId(),
                            mission.getContent(),
                            todayMission.getTargetDate(),
                            todayMission.getMissionStatus()
                    );
                })
                .toList();
    }

    @Override
    public TodayMissionInfo findByUserIdAndTodayMissionId(UserId userId, String todayMissionId) {
        List<Object[]> results = todayMissionJpaRepository.findWithMissionByIdAndUserId(todayMissionId, userId.getId());

        if (results.isEmpty()) {
            throw new IllegalArgumentException("해당 유저 미션이 존재하지 않거나 권한이 없습니다.");
        }

        Object[] row = results.get(0);
        TodayMissionJpaEntity entity = (TodayMissionJpaEntity) row[0];
        CleaningMissionJpaEntity mission = (CleaningMissionJpaEntity) row[1];

        return TodayMissionInfo.of(
                entity.getMissionId(),
                mission.getContent(),
                entity.getTargetDate(),
                entity.getMissionStatus()
        );
    }
}
