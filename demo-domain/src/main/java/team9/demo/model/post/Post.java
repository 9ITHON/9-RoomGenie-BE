package team9.demo.model.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.comment.Comment;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Post {

    private final PostInfo postInfo;
    private final List<Comment> comments;
    private final List<PostDetail> postDetails;

    public static Post of(PostInfo postInfo, List<Comment> comments, List<PostDetail> postDetails) {
        return new Post(postInfo, comments, postDetails);
    }



}
