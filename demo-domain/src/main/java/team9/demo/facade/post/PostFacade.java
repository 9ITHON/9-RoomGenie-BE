package team9.demo.facade.post;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.implementation.comment.CommentReader;
import team9.demo.implementation.post.PostEnricher;
import team9.demo.implementation.post.PostReader;
import team9.demo.model.comment.Comment;
import team9.demo.model.post.Post;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostId;
import team9.demo.model.post.PostInfo;
import team9.demo.model.user.UserId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostReader postReader;
    private final PostEnricher postEnricher;
    private final CommentReader commentReader;

    public List<Post> getPosts(UserId userId, List<UserId> targetUserIds) {
        List<PostInfo> posts = postReader.readInfos(targetUserIds);
        List<PostId> visiblePostIds = postReader.readVisiblePostIds(
                userId,
                posts.stream().map(PostInfo::postId).toList()
        );
        List<PostDetail> postsDetails = postReader.readsDetails(visiblePostIds);
        List<Comment> comments = commentReader.readByPostIds(visiblePostIds);
        return postEnricher.enriches(posts, visiblePostIds, postsDetails, comments);
    }
}
