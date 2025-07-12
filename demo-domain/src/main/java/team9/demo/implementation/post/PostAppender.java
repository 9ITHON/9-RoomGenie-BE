package team9.demo.implementation.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.model.media.Media;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;
import team9.demo.repository.post.PostRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostAppender {

    private final PostRepository postRepository;


    @Transactional
    public PostId append(List<Media> medias, UserId userId, String title, String content) {
        // medias에서 beforeImageUrl, afterImageUrl 뽑기 (예: 첫번째, 두번째 이미지)
        String beforeImageUrl = medias.size() > 0 ? medias.get(0).getUrl() : null;
        String afterImageUrl = medias.size() > 1 ? medias.get(1).getUrl() : null;

        // Post 엔티티 저장 시 URL 필드 포함
        PostId postId = postRepository.append(userId, title, content, beforeImageUrl, afterImageUrl);

        return postId;
    }

}