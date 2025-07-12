package team9.demo.jpaentity.push;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.notification.NotificationStatus;
import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "push_notification",
        schema = "roomgenie",
        indexes = {
                @Index(name = "push_notification_idx_device_provider", columnList = "deviceId, provider"),
                @Index(name = "push_notification_idx_user_id", columnList = "userId"),
                @Index(name = "push_notification_idx_app_token_user_id", columnList = "appToken, userId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotificationJpaEntity {

    @Id
    @Column(name = "push_notification_id")
    private String pushId = UUID.randomUUID().toString();

    private String appToken;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private PushInfo.Provider provider;

    @Enumerated(EnumType.STRING)
    private NotificationStatus chatStatus = NotificationStatus.ALLOWED;

    @Enumerated(EnumType.STRING)
    private NotificationStatus scheduleStatus = NotificationStatus.ALLOWED;

    private String userId;

    private LocalDateTime deadline;

    @Builder
    public PushNotificationJpaEntity(
            String appToken,
            String deviceId,
            PushInfo.Provider provider,
            NotificationStatus chatStatus,
            NotificationStatus scheduleStatus,
            String userId,
            LocalDateTime deadline
    ) {
        this.pushId = UUID.randomUUID().toString();
        this.appToken = appToken;
        this.deviceId = deviceId;
        this.provider = provider;
        this.chatStatus = chatStatus != null ? chatStatus : NotificationStatus.ALLOWED;
        this.scheduleStatus = scheduleStatus != null ? scheduleStatus : NotificationStatus.ALLOWED;
        this.userId = userId;
        this.deadline = deadline;
    }

    public static PushNotificationJpaEntity generate(String appToken, PushInfo.Device device, UserInfo userInfo) {
        return PushNotificationJpaEntity.builder()
                .appToken(appToken)
                .deviceId(device.getDeviceId())
                .provider(device.getProvider())
                .userId(userInfo.getUserId().getId())
                // 데드라인도 설정
                .build();
    }

    public PushInfo toPushToken() {
        return PushInfo.of(
                this.pushId,
                this.appToken,
                this.deviceId,
                this.provider,
                UserId.of(this.userId),
                this.chatStatus,
                this.scheduleStatus
        );
    }

    public void updateChatStatus(NotificationStatus status) {
        this.chatStatus = status;
    }

    public void updateScheduleStatus(NotificationStatus status) {
        this.scheduleStatus = status;
    }

    public void updateAppToken(String appToken) {
        this.appToken = appToken;
    }
}