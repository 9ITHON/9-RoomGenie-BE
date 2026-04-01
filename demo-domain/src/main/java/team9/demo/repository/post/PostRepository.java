package team9.demo.repository.post;

import team9.demo.model.post.PostId;
import team9.demo.model.post.PostInfo;
import team9.demo.model.user.UserId;

import java.util.List;

public interface PostRepository {
    PostId append(UserId userId, String title, String content);


    List<PostInfo> readInfos(List<UserId> targetUserIds);


}
