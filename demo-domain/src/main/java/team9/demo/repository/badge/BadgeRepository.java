package team9.demo.repository.badge;

import java.util.List;

public interface BadgeRepository {
    List<String> findEarnedBadgeIds(long point);
    List<String> findAlreadyGrantedBadgeIds(String userId, List<String> badgeIds);
    void grantBadges(String userId, List<String> badgeIds);
}
