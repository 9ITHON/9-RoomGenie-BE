package team9.demo.dto.response.post;

import team9.demo.model.post.PostId;

public class PostIdResponse {

    private final String postId;

    private PostIdResponse(String postId) {
        this.postId = postId;
    }

    public static PostIdResponse of(PostId postId) {
        return new PostIdResponse(postId.getId());
    }

    public String getPostId() {
        return postId;
    }
}
