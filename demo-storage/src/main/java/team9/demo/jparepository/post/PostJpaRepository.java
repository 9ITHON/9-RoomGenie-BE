package team9.demo.jparepository.post;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import team9.demo.jpaentity.post.PostJpaEntity;
import team9.demo.model.post.PostInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, String> {

    List<PostJpaEntity> findAllByUserIdInAndCreatedAtAfter(List<String> userIds, LocalDateTime createdAt, Sort sort);

}