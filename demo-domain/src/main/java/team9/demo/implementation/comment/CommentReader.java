package team9.demo.implementation.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.model.comment.Comment;
import team9.demo.model.post.PostId;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentReader {

    private final CommentRepository commentRepository;

    public List<Comment> readByPostIds(List<PostId> postIds) {
        return commentRepository.findByPostIds(postIds);
    }
}
