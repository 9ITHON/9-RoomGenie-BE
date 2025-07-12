package team9.demo.jparepository.badge;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.badge.BadgeJpaEntity;

import java.util.List;

public interface BadgeJpaRepository extends JpaRepository<BadgeJpaEntity, String> {

    // 현재 포인트 이하 배지 전부 조회
    List<BadgeJpaEntity> findAllByRequiredPointLessThanEqual(Long point);
}