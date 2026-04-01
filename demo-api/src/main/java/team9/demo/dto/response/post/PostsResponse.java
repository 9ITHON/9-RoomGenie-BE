package team9.demo.dto.response.post;

import team9.demo.model.post.Post;

import java.util.List;
import java.util.stream.Collectors;

public record PostsResponse(
        List<PostResponse> posts
) {
    public static PostsResponse of(List<Post> posts) {
        List<PostResponse> response = posts.stream()
                .map(PostResponse::of)
                .collect(Collectors.toList());
        return new PostsResponse(response);
    }


}
