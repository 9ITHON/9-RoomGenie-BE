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
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;
import team9.demo.repository.mission.TodayMissionRepository;
import team9.demo.repository.user.UserRepository;

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
                .orElseThrow(() -> new IllegalArgumentException("해당 미션이 존재하지 않습니다."));
        entity.updateStatus(status); // 엔티티 내에서 상태 변경 메서드 제공
        todayMissionJpaRepository.save(entity); // ✅ 명시적으로 save 호출

        // ✅ 상태가 COMPLETED일 경우 코인 +5
        if (status == MissionStatus.COMPLETED) {
            String userId = entity.getUserId(); // TodayMissionJpaEntity에 userId 필드가 있다고 가정
            UserJpaEntity user = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
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
