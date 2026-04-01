package team9.demo.repository.jpa.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.post.PostVisibilityId;
import team9.demo.jpaentity.post.PostVisibilityJpaEntity;
import team9.demo.jparepository.post.PostVisibilityJpaRepository;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;
import team9.demo.repository.post.PostVisibilityRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostVisibilityRepositoryImpl implements PostVisibilityRepository {

    private final PostVisibilityJpaRepository postVisibilityJpaRepository;

    @Override
    public void append(PostId postId, List<UserId> targetUserIds){
        var entities = targetUserIds.stream()
                        .map(userId -> PostVisibilityJpaEntity.generate(postId, userId))
                        .toList();

        postVisibilityJpaRepository.saveAll(entities);
    }

    @Override
    public List<PostId> readVisiblePostIds(UserId userId, List<PostId> postIds){
        List<PostVisibilityId> postVisibilityIds = postIds.stream()
                .map(postId -> PostVisibilityId.of(postId, userId))
                .toList();

        return postVisibilityJpaRepository.findAllByIdIn(postVisibilityIds)
                .stream()
                .map(PostVisibilityJpaEntity::getPostId)
                .toList();
    }



}
