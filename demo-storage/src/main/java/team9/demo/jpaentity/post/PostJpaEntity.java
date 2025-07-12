package team9.demo.jpaentity.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.util.UUID;

@Entity
@Table(name = "post", schema = "roomgenie",
        indexes = {
                @Index(name = "post_idx_user_id", columnList = "userId"),
                @Index(name = "post_idx_user_id_created_at", columnList = "userId, created_at"),
                @Index(name = "post_idx_post_id_user_id", columnList = "postId, userId")
        })
@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpaEntity extends BaseEntity {

    @Id
    private String postId;

    private String title;

    @Lob
    private String content;

    private String userId;

    @Column(name = "cheer_count")
    @ColumnDefault("0")
    private Long cheerCount;

    private String beforeImageUrl;

    private String afterImageUrl;

    @Builder
    public PostJpaEntity(String postId, String title, String content, String userId, Long cheerCount, String beforeImageUrl, String afterImageUrl) {
        this.postId = postId != null ? postId : UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.cheerCount = cheerCount != null ? cheerCount : 0L;
        this.beforeImageUrl = beforeImageUrl;
        this.afterImageUrl = afterImageUrl;
    }

    public static PostJpaEntity generate(String title, String content, UserId userId, String beforeImageUrl, String afterImageUrl) {
        return PostJpaEntity.builder()
                .title(title)
                .content(content)
                .userId(userId.getId())
                .cheerCount(0L)
                .beforeImageUrl(beforeImageUrl)
                .afterImageUrl(afterImageUrl)
                .build();
    }

    public PostId toPostId() {
        return PostId.of(postId);
    }
}
