package team9.demo.jpaentity.mission;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import team9.demo.jpaentity.common.BaseEntity;
import team9.demo.model.mission.MissionId;
import team9.demo.model.mission.MissionStatus;
import team9.demo.model.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "today_mission", schema = "roomgenie",
        indexes = {
                @Index(name = "today_mission_idx_user_id", columnList = "userId, todayMissionId"),
                @Index(name = "today_mission_idx_mission_id", columnList = "missionId, todayMissionId"),

        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodayMissionJpaEntity extends BaseEntity {

    @Id
    @Column(name = "today_mission_id", nullable = false)
    private String todayMissionId = UUID.randomUUID().toString();

    @Column(name = "target_date", nullable = false)
    private LocalDateTime targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status")
    private MissionStatus missionStatus;

    private String userId;

    private String missionId;

    @Builder
    public TodayMissionJpaEntity(LocalDateTime targetDate, String userId, String missionId, MissionStatus missionStatus) {
        this.todayMissionId = UUID.randomUUID().toString();
        this.targetDate = targetDate;
        this.userId = userId;
        this.missionId = missionId;
        this.missionStatus = missionStatus;
    }

    public static TodayMissionJpaEntity generate(LocalDateTime targetDate, String userId, String missionId) {
        return TodayMissionJpaEntity.builder()
                .targetDate(targetDate)
                .userId(userId)
                .missionId(missionId)
                .missionStatus(MissionStatus.ONGOING)
                .build();
    }

    public void updateStatus(MissionStatus status) {
        this.missionStatus = status;
    }


}
