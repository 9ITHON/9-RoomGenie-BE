package team9.demo.jpaentity.post;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PostVisibilityId implements Serializable {

    @Column
    private String postId;

    @Column
    private String userId;

    public static PostVisibilityId of(PostId postId, UserId userId) {
        return new PostVisibilityId(postId.getId(), userId.getId());
    }


}
