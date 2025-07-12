package team9.demo.jpaentity.badge;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.mission.MissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "user_badge", schema = "roomgenie",
        indexes = {
                @Index(name = "user_badge_idx_user_id", columnList = "userId, userBadgeId"),
                @Index(name = "user_badge_idx_badge_id", columnList = "badgeId, BadgeId"),

        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadgeJpaEntity extends BaseEntity {

    @Id
    @Column(name = "user_badge_id", nullable = false)
    private String userBadgeId = UUID.randomUUID().toString();

    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    private String userId;

    private String badgeId;

    @Builder
    public UserBadgeJpaEntity(LocalDateTime grantedAt, String userId, String badgeId) {
        this.userBadgeId = UUID.randomUUID().toString();
        this.grantedAt = grantedAt;
        this.userId = userId;
        this.badgeId = badgeId;
    }

    public static team9.demo.jpaentity.badge.UserBadgeJpaEntity generate(LocalDateTime grantedAt, String userId, String badgeId)  {
        return UserBadgeJpaEntity.builder()
                .userId(userId)
                .badgeId(badgeId)
                .grantedAt(grantedAt)
                .build();
    }


}
