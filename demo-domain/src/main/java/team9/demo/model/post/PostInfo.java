package team9.demo.model.post;

import team9.demo.model.user.UserId;

import java.time.LocalDateTime;

public record PostInfo (
        PostId postId,
        UserId userId,
        LocalDateTime uploadAt,
        String title,
        String content,
        Long cheerCount
){

    public static PostInfo of(PostId postId, UserId userId, LocalDateTime uploadAt, String title, String content, Long cheerCount) {
        return new PostInfo(postId, userId, uploadAt, title, content, cheerCount);
    }

    public PostId getPostId() { return postId; }
}
