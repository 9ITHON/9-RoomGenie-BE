package team9.demo.jparepository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.post.PostJpaEntity;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, String> {
}