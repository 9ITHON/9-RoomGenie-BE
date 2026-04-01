package team9.demo.jparepository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.post.PostVisibilityId;
import team9.demo.jpaentity.post.PostVisibilityJpaEntity;

import java.util.List;

public interface PostVisibilityJpaRepository extends JpaRepository<PostVisibilityJpaEntity, PostVisibilityId> {
    List<PostVisibilityJpaEntity> findAllByIdIn(List<PostVisibilityId> postVisibilityIds);
}
