package team9.demo.jparepository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.comment.CommentJpaEntity;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, String> {
    List<CommentJpaEntity> findAllByPostIdIn(List<String> postIds);
}
