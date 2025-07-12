package team9.demo.repository.post;

import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

public interface PostRepository {
    PostId append(UserId userId, String title, String content, String beforeImageUrl, String afterImageUrl);
}
