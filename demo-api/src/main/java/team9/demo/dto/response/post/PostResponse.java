package team9.demo.dto.response.post;

import lombok.Builder;
import lombok.Getter;
import team9.demo.dto.response.comment.CommentResponse;
import team9.demo.model.comment.Comment;
import team9.demo.model.post.Post;
import team9.demo.model.post.PostInfo;
import team9.demo.model.user.UserId;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PostResponse(
        String postId,
        String uploadTime,
        String title,
        String content,
        List<PostDetailResponse> postDetails,
        List<CommentResponse> comments

) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static PostResponse of(Post post){
        List<PostDetailResponse> details = post.getPostDetails().stream().map(PostDetailResponse::of).toList();
        List<CommentResponse> comments = post.getComments().stream().map(CommentResponse::of).toList();



        return new PostResponse(
            post.getPostInfo().postId().getId(),
            post.getPostInfo().uploadAt().format(FORMATTER),
            post.getPostInfo().title(),
            post.getPostInfo().content(),
            details,
            comments

        );
    }


}
