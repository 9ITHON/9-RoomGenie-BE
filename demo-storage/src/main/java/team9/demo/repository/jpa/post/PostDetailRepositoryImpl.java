package team9.demo.repository.jpa.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team9.demo.jpaentity.post.PostDetailJpaEntity;
import team9.demo.jparepository.post.PostDetailJpaRepository;
import team9.demo.model.media.Media;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostId;
import team9.demo.repository.post.PostDetailRepository;
import team9.demo.util.SortType;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostDetailRepositoryImpl implements PostDetailRepository {

    private final PostDetailJpaRepository postDetailJpaRepository;

    @Override
    public void append(List<Media> medias, PostId postId){
        postDetailJpaRepository.saveAll(PostDetailJpaEntity.generate(medias, postId));
    };

    @Override
    public List<PostDetail> readsDetails(List<PostId> postIds){
        List<String> ids = postIds.stream()
                .map(PostId::getId)
                .toList();

        List<PostDetailJpaEntity> postDetail = postDetailJpaRepository.findByPostIdIn(
                ids,
                SortType.SMALLEST.toSort()
        );

        return postDetail.stream()
                .map(PostDetailJpaEntity::toPostDetail)
                .toList();
    }


}
