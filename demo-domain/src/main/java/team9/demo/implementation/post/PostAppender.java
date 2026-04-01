package team9.demo.implementation.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team9.demo.model.media.Media;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;
import team9.demo.repository.post.PostDetailRepository;
import team9.demo.repository.post.PostRepository;
import team9.demo.repository.post.PostVisibilityRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostAppender {

    private final PostRepository postRepository;
    private final PostVisibilityRepository postVisibilityRepository;
    private final PostDetailRepository postDetailRepository;

    @Transactional
    public PostId append(List<Media> medias, UserId userId, String title, String content) {
        // Post 엔티티 저장 시 URL 필드 포함
        PostId postId = postRepository.append(userId, title, content);
        postDetailRepository.append(medias, postId);

        return postId;
    }

    @Transactional
    public void appendVisibility(PostId postId, List<UserId> targetUserIds) {
        postVisibilityRepository.append(postId, targetUserIds);
    }

}