package team9.demo.repository.post;

import team9.demo.model.media.Media;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostId;

import java.util.List;

public interface PostDetailRepository {

    void append(List<Media> medias, PostId postId);
    List<PostDetail> readsDetails(List<PostId> postIds);
}
