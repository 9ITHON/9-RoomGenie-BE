package team9.demo.implementation.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostId;
import team9.demo.model.post.PostInfo;
import team9.demo.model.user.UserId;
import team9.demo.repository.post.PostDetailRepository;
import team9.demo.repository.post.PostRepository;
import team9.demo.repository.post.PostVisibilityRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostReader {

    private final PostRepository postRepository;
    private final PostVisibilityRepository postVisibilityRepository;
    private final PostDetailRepository postDetailRepository;

    public List<PostInfo> readInfos (List<UserId> targetUserIds){
        return postRepository.readInfos(targetUserIds);
    }

    public List<PostId> readVisiblePostIds(UserId userId, List<PostId> postIds){
        return postVisibilityRepository.readVisiblePostIds(userId, postIds);
    }

    public List<PostDetail> readsDetails(List<PostId> postIds){
        return postDetailRepository.readsDetails(postIds);
    }


}
