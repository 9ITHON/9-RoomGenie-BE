package team9.demo.repository.jpa.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.post.PostJpaEntity;
import team9.demo.jparepository.post.PostJpaRepository;
import team9.demo.model.post.PostId;
import team9.demo.model.post.PostInfo;
import team9.demo.model.user.UserId;
import team9.demo.repository.post.PostRepository;
import team9.demo.util.SortType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {


    private final PostJpaRepository postJpaRepository;


    @Override
    public PostId append(UserId userId, String title, String content) {
        PostJpaEntity entity = PostJpaEntity.generate(title, content, userId);
        postJpaRepository.save(entity);
        return entity.toPostId();
    }


    @Override
    public List<PostInfo> readInfos(List<UserId> targetUserIds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(1);

        List<String> userIds = targetUserIds.stream()
                .map(UserId::getId)
                .toList();

        return postJpaRepository.findAllByUserIdInAndCreatedAtAfter(
                userIds,
                startDate,
                SortType.LATEST.toSort()
        )
                .stream()
                .map(PostJpaEntity::toPostInfo)
                .toList();
    }


}
