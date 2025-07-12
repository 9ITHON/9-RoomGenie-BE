package team9.demo.jpaentity.analysis;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.user.UserId;

import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "analysis_result", schema = "roomgenie",
        indexes = {
            @Index(name = "analysis_idx_result_id_user_id", columnList = "resultId, userId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisResultJpaEntity extends BaseEntity {

    @Id
    private String resultId = UUID.randomUUID().toString();

    private String userId;

    @Lob
    private String resultText;


    private String imageUrl;

    @Builder
    public AnalysisResultJpaEntity(String userId, String resultText, String imageUrl) {
        this.resultId = UUID.randomUUID().toString();
        this.userId = userId;
        this.resultText = resultText;
        this.imageUrl = imageUrl;

    }

    public static AnalysisResultJpaEntity generate(UserId userId, String resultText, String imageUrl)  {
        return AnalysisResultJpaEntity.builder()
                .userId(userId.getId())
                .resultText(resultText)
                .imageUrl(imageUrl)
                .build();
    }
}