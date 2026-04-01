package team9.demo.model.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.comment.Comment;
import team9.demo.model.media.Media;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostDetail {
     // 애매

     private final PostDetailId postDetailId;
     private final Media media;
     private final PostId postId;

     public static PostDetail of(PostDetailId postDetailId, Media media, PostId postId) {
          return new PostDetail(postDetailId, media, postId);
     }



}
