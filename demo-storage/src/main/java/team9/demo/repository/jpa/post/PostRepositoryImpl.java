package team9.demo.repository.jpa.post;

import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.post.PostJpaEntity;
import team9.demo.jparepository.post.PostJpaRepository;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;
import team9.demo.repository.post.PostRepository;

@Repository
public class PostRepositoryImpl implements PostRepository {


    private final PostJpaRepository postJpaRepository;

    public PostRepositoryImpl(PostJpaRepository postJpaRepository) {
        this.postJpaRepository = postJpaRepository;
    }


    @Override
    public PostId append(UserId userId, String title, String content, String beforeImageUrl, String afterImageUrl) {
        PostJpaEntity entity = PostJpaEntity.generate(title, content, userId, beforeImageUrl, afterImageUrl);
        postJpaRepository.save(entity);
        return entity.toPostId();
    }
}
