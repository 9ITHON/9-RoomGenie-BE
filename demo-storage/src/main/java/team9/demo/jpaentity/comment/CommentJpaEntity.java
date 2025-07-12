package team9.demo.jpaentity.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.jpaentity.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(
        name = "comment",
        schema = "roomgenie",
        indexes = {
                @Index(name = "comment_idx_post_id", columnList = "postId"),
                @Index(name = "comment_idx_user_id", columnList = "userId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentJpaEntity extends BaseEntity {

    @Id
    private String commentId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String userId;

    @Lob
    private String content;

    @Builder
    public CommentJpaEntity(String postId, String userId, String content) {
        this.commentId = UUID.randomUUID().toString();
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public static CommentJpaEntity generate(String postId, String userId, String content) {
        return CommentJpaEntity.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();
    }
}
