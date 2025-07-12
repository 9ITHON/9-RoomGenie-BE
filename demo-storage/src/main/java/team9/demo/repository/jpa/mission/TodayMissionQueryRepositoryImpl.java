package team9.demo.repository.jpa.mission;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.mission.CleaningMissionJpaEntity;
import team9.demo.jpaentity.mission.TodayMissionJpaEntity;
import team9.demo.jparepository.mission.CleaningMissionJpaRepository;
import team9.demo.jparepository.mission.TodayMissionJpaRepository;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.TodayMissionQueryRepository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodayMissionQueryRepositoryImpl implements TodayMissionQueryRepository {

    private final TodayMissionJpaRepository todayMissionJpaRepository;
    private final CleaningMissionJpaRepository cleaningMissionJpaRepository;

    @Override
    public List<TodayMissionInfo> findAllByUserId(UserId userId) {
        List<TodayMissionJpaEntity> missions = todayMissionJpaRepository.findAllByUserId(userId.getId());

        return missions.stream()
                .map(todayMission -> {
                    CleaningMissionJpaEntity mission = cleaningMissionJpaRepository.findById(todayMission.getMissionId())
                            .orElseThrow(() -> new NotFoundException("해당 미션이 존재하지 않습니다: " + todayMission.getMissionId()));

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
        TodayMissionJpaEntity entity = todayMissionJpaRepository.findById(todayMissionId)
                .filter(e -> e.getUserId().equals(userId.getId()))
                .orElseThrow(() -> new NotFoundException("해당 유저 미션이 존재하지 않거나 권한이 없습니다."));

        CleaningMissionJpaEntity mission = cleaningMissionJpaRepository.findById(entity.getMissionId())
                .orElseThrow(() -> new NotFoundException("해당 미션 내용이 존재하지 않습니다."));

        return TodayMissionInfo.of(
                entity.getMissionId(),
                mission.getContent(),
                entity.getTargetDate(),
                entity.getMissionStatus()
        );
    }
}
