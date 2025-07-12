package team9.demo.jparepository.badge;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.badge.UserBadgeJpaEntity;

public interface UserBadgeJpaRepository extends JpaRepository<UserBadgeJpaEntity, String> {

    // 중복 배지 지급 방지
    boolean existsByUserIdAndBadgeId(String userId, String badgeId);
}
