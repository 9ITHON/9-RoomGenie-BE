package team9.demo.implementation.post;

import org.springframework.stereotype.Component;
import team9.demo.model.comment.Comment;
import team9.demo.model.post.Post;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostId;
import team9.demo.model.post.PostInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostEnricher {

    public List<Post> enriches(List<PostInfo> posts, List<PostId> visiblePostIds, List<PostDetail> postDetails, List<Comment> comments){
        Map<PostId, List<PostDetail>> postDetailMap = postDetails.stream()
                .collect(Collectors.groupingBy(PostDetail::getPostId));

        Map<PostId, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(Comment::getPostId));


        Set<PostId> visibleSet = Set.copyOf(visiblePostIds);

        return posts.stream()
                .filter(f -> visibleSet.contains(f.getPostId()))
                .map(postInfo -> {
                    List<PostDetail> details = postDetailMap.getOrDefault(postInfo.getPostId(), List.of());
                    List<Comment> postComments = commentMap.getOrDefault(postInfo.getPostId(), List.of());
                    return Post.of(postInfo, postComments, details);
                })
                .collect(Collectors.toList());

    }



}
