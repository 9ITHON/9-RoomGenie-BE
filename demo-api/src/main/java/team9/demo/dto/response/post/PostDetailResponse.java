package team9.demo.dto.response.post;

import team9.demo.model.comment.Comment;
import team9.demo.model.media.Media;
import team9.demo.model.post.Post;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostDetailId;
import team9.demo.model.post.PostId;

import java.util.List;

public record PostDetailResponse (
    int index,
    String fileUrl,
    String type
){

    public static PostDetailResponse of(PostDetail postDetail) {
        return new PostDetailResponse(
            postDetail.getMedia().getIndex(),
                postDetail.getMedia().getUrl(),
                postDetail.getMedia().getType().value().toLowerCase()
        );
    }

}
