package team9.demo.jpaentity.badge;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;

import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "badge",
        schema = "roomgenie"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadgeJpaEntity extends BaseEntity {

    @Id
    private String badgeId = UUID.randomUUID().toString();

    private String badgeName;
    private Long requiredPoint;
    private String imageUrl;

    @Builder
    public BadgeJpaEntity(
            String badgeName,
            Long requiredPoint,
            String imageUrl
    ) {
        this.badgeId = UUID.randomUUID().toString();
        this.badgeName = badgeName;
        this.requiredPoint = requiredPoint;
        this.imageUrl = imageUrl;
    }


    public static BadgeJpaEntity generate(String badgeName, Long requiredPoint, String imageUrl) {
        return BadgeJpaEntity.builder()
                .badgeName(badgeName)
                .requiredPoint(requiredPoint)
                .imageUrl(imageUrl)
                .build();
    }





}