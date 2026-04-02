package team9.demo.repository.jpa.badge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.badge.BadgeJpaEntity;
import team9.demo.jpaentity.badge.UserBadgeJpaEntity;
import team9.demo.jparepository.badge.BadgeJpaRepository;
import team9.demo.jparepository.badge.UserBadgeJpaRepository;
import team9.demo.repository.badge.BadgeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BadgeRepositoryImpl implements BadgeRepository {

    private final BadgeJpaRepository badgeJpaRepository;
    private final UserBadgeJpaRepository userBadgeJpaRepository;

    @Override
    public List<String> findEarnedBadgeIds(long point) {
        return badgeJpaRepository.findAllByRequiredPointLessThanEqual(point)
                .stream()
                .map(BadgeJpaEntity::getBadgeId)
                .toList();
    }

    @Override
    public List<String> findAlreadyGrantedBadgeIds(String userId, List<String> badgeIds) {
        return badgeIds.stream()
                .filter(badgeId -> userBadgeJpaRepository.existsByUserIdAndBadgeId(userId, badgeId))
                .toList();
    }

    @Override
    public void grantBadges(String userId, List<String> badgeIds) {
        List<UserBadgeJpaEntity> entities = badgeIds.stream()
                .map(badgeId -> UserBadgeJpaEntity.generate(LocalDateTime.now(), userId, badgeId))
                .toList();
        userBadgeJpaRepository.saveAll(entities);
    }
}
