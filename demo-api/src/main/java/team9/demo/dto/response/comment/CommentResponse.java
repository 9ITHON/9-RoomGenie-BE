package team9.demo.dto.response.comment;

import lombok.Builder;
import team9.demo.model.comment.Comment;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.time.format.DateTimeFormatter;


public record CommentResponse (
        String userId,
        String postId,
        String content,
        String uploadTime

){
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                comment.getUserId().getId(),
                comment.getPostId().getId(),
                comment.getContent(),
                comment.getUploadAt().format(FORMATTER)
        );
    }

}
