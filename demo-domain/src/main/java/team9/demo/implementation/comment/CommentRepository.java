package team9.demo.implementation.comment;

import team9.demo.model.comment.Comment;
import team9.demo.model.post.PostId;

import java.util.List;

public interface CommentRepository {
    List<Comment> findByPostIds(List<PostId> postIds);
}
