package team9.demo.repository.jpa.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.implementation.comment.CommentRepository;
import team9.demo.jpaentity.comment.CommentJpaEntity;
import team9.demo.jparepository.comment.CommentJpaRepository;
import team9.demo.model.comment.Comment;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<Comment> findByPostIds(List<PostId> postIds) {
        List<String> ids = postIds.stream().map(PostId::getId).toList();
        return commentJpaRepository.findAllByPostIdIn(ids).stream()
                .map(entity -> Comment.of(
                        UserId.of(entity.getUserId()),
                        PostId.of(entity.getPostId()),
                        entity.getContent(),
                        entity.getCreatedAt()
                ))
                .toList();
    }
}
