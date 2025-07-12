package team9.demo.jpaentity.reward;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.reward.RewardType;

import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "reward", schema = "roomgenie",
        indexes = {
            @Index(name = "reward_idx_user_id", columnList = "userId, rewardId"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardJpaEntity extends BaseEntity {

    @Id
    @Column(name = "reward_id")
    private String rewardId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RewardType type;

    private Integer amount;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Builder
    public RewardJpaEntity(RewardType type, Integer amount, String content, String userId) {
        this.rewardId = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.content = content;
        this.userId = userId;
    }

    public static RewardJpaEntity generate(RewardType type, Integer amount, String content, String userId) {
        return RewardJpaEntity.builder()
                .type(type)
                .amount(amount)
                .content(content)
                .userId(userId)
                .build();
    }
}