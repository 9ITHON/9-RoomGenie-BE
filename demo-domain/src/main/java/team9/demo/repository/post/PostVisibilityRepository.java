package team9.demo.repository.post;

import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.util.List;

public interface PostVisibilityRepository {
    void append(PostId postId, List<UserId> targetUserIds);
    List<PostId> readVisiblePostIds(UserId userId, List<PostId> postIds);
}
