package team9.demo.jpaentity.post;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.Media;
import team9.demo.model.media.MediaType;
import team9.demo.model.post.PostDetail;
import team9.demo.model.post.PostDetailId;
import team9.demo.model.post.PostId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "post_detail",
        schema = "roomgenie",
        indexes = {
                @Index(name = "post_detail_idx_post_id", columnList = "postId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetailJpaEntity {

    @Id
    @Column
    private String postDetailId = UUID.randomUUID().toString();

    @Column
    private int sequence;

    @Column
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType fileType;

    @Column(nullable = false)
    private String postId;

    private PostDetailJpaEntity(
            int sequence,
            String fileUrl,
            MediaType fileType,
            String postId) {
        this.sequence = sequence;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.postId = postId;
    }

    public static List<PostDetailJpaEntity> generate(List<Media> medias, PostId postId) {
        return medias.stream()
                .map(media -> new PostDetailJpaEntity(
                        media.getIndex(),
                        media.getUrl(),
                        media.getType(),
                        postId.getId()
                ))
                .collect(Collectors.toList());
    }

    public PostDetail toPostDetail() {
        return PostDetail.of(
                PostDetailId.of(postDetailId),
                Media.of(FileCategory.POST, fileUrl, sequence, fileType),
                PostId.of(postId)
        );

    }

}
