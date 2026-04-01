package team9.demo.jpaentity.post;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@Entity
@Table(name = "post_visibility", schema = "roomgenie")
public class PostVisibilityJpaEntity {

    @EmbeddedId
    private PostVisibilityId id;

    public static PostVisibilityJpaEntity generate(PostId postId, UserId userId) {
        return new PostVisibilityJpaEntity(PostVisibilityId.of(postId, userId));
    }

    public PostId getPostId() {
        return PostId.of(id.getPostId());
    }

}
