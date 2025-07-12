package team9.demo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.jpaentity.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(
        name = "like_post",
        schema = "roomgenie",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_post_user", columnNames = {"postId", "userId"})
        },
        indexes = {
                @Index(name = "like_post_idx_post_id", columnList = "postId"),
                @Index(name = "like_post_idx_user_id", columnList = "userId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikePostJpaEntity extends BaseEntity {

    @Id
    private String likeId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String userId;

    @Builder
    public LikePostJpaEntity(String postId, String userId) {
        this.likeId = UUID.randomUUID().toString();
        this.postId = postId;
        this.userId = userId;
    }

    public static LikePostJpaEntity generate(String postId, String userId) {
        return LikePostJpaEntity.builder()
                .postId(postId)
                .userId(userId)
                .build();
    }
}
