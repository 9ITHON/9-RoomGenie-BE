package team9.demo.dto.response.post;

import team9.demo.model.post.PostId;


public record PostIdResponse(String postId) {
    public static PostIdResponse of(PostId postId) {
        return new PostIdResponse(postId.getId());
    }
}
