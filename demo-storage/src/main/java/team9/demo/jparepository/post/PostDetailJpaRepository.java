package team9.demo.jparepository.post;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.post.PostDetailJpaEntity;

import java.util.List;

public interface PostDetailJpaRepository extends JpaRepository<PostDetailJpaEntity, String> {

    List<PostDetailJpaEntity> findByPostIdIn(List<String> postId, Sort sort);

}
