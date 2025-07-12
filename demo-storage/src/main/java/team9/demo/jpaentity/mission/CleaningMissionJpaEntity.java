package team9.demo.jpaentity.mission;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "cleaning_mission", schema = "roomgenie"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CleaningMissionJpaEntity extends BaseEntity {

    @Id
    @Column(name = "mission_id", nullable = false)
    private String missionId = UUID.randomUUID().toString();

    @Column(name = "target_date", nullable = false)
    private LocalDateTime targetDate;

    @Column(length = 255)
    private String content;

    @Builder
    public CleaningMissionJpaEntity(LocalDateTime targetDate, String content) {
        this.missionId = UUID.randomUUID().toString();
        this.targetDate = targetDate;
        this.content = content;
    }

    public static CleaningMissionJpaEntity generate(LocalDateTime targetDate, String content) {
        return CleaningMissionJpaEntity.builder()
                .targetDate(targetDate)
                .content(content)
                .build();
    }
}

