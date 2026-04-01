package team9.demo.repository.jpa.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.jpaentity.badge.BadgeJpaEntity;
import team9.demo.jpaentity.badge.UserBadgeJpaEntity;
import team9.demo.jpaentity.mission.TodayMissionJpaEntity;
import team9.demo.jpaentity.user.UserJpaEntity;
import team9.demo.jparepository.badge.BadgeJpaRepository;
import team9.demo.jparepository.badge.UserBadgeJpaRepository;
import team9.demo.jparepository.mission.TodayMissionJpaRepository;
import team9.demo.jparepository.user.UserJpaRepository;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.TodayMissionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodayMissionRepositoryImpl implements TodayMissionRepository {

    private final TodayMissionJpaRepository todayMissionJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BadgeJpaRepository badgeJpaRepository;
    private final UserBadgeJpaRepository userBadgeJpaRepository;

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
    @Transactional
    @Override
    public void updateStatus(String todayMissionId, MissionStatus status) {
        TodayMissionJpaEntity entity = todayMissionJpaRepository.findById(todayMissionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MISSION_NOT_FOUND));
        entity.updateStatus(status);
        todayMissionJpaRepository.save(entity);

        if (status == MissionStatus.COMPLETED) {
            String userId = entity.getUserId();
            UserJpaEntity user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
            user.increasePoint(5L);
            userJpaRepository.save(user);

            // 3. 지급 조건을 만족하는 배지 목록 가져오기
            List<BadgeJpaEntity> earnedBadges = badgeJpaRepository.findAllByRequiredPointLessThanEqual(user.getPoint());

            // 4. 중복 체크 및 지급
            for (BadgeJpaEntity badge : earnedBadges) {
                boolean alreadyGranted = userBadgeJpaRepository.existsByUserIdAndBadgeId(userId, badge.getBadgeId());
                if (!alreadyGranted) {
                    UserBadgeJpaEntity badgeEntity = UserBadgeJpaEntity.generate(LocalDateTime.now(), userId, badge.getBadgeId());
                    userBadgeJpaRepository.save(badgeEntity);
                }
            }
        }
    }

}
