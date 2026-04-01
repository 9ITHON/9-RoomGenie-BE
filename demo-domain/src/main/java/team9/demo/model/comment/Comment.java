package team9.demo.model.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import team9.demo.model.post.PostId;
import team9.demo.model.user.UserId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Comment {
    private final UserId userId;
    private final PostId postId;
    private final String content;
    private final LocalDateTime uploadAt;


    public static Comment of(UserId userId, PostId postId, String content, LocalDateTime uploadAt) {
        return new Comment(userId, postId, content, uploadAt);
    }
}
