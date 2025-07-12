package team9.demo.model.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team9.demo.model.user.UserId;

@Getter
public final class PushInfo {

    private final String pushId;
    private final String pushToken;
    private final Device device;
    private final UserId userId;
    private final NotificationStatusInfo statusInfo;

    private PushInfo(
            String pushId,
            String pushToken,
            Device device,
            UserId userId,
            NotificationStatusInfo statusInfo
    ) {
        this.pushId = pushId;
        this.pushToken = pushToken;
        this.device = device;
        this.userId = userId;
        this.statusInfo = statusInfo;
    }

    public static PushInfo of(
            String pushTokenId,
            String fcmToken,
            String deviceId,
            Provider provider,
            UserId userId,
            NotificationStatus chatStatus,
            NotificationStatus scheduleStatus
    ) {
        return new PushInfo(
                pushTokenId,
                fcmToken,
                Device.of(deviceId, provider),
                userId,
                new NotificationStatusInfo(chatStatus, scheduleStatus)
        );
    }

    // ✅ 내부 정적 클래스: Device
    @Getter
    @RequiredArgsConstructor
    public static class Device {
        private final String deviceId;
        private final Provider provider;

        public static Device of(String deviceId, Provider provider) {
            return new Device(deviceId, provider);
        }
    }

    // ✅ 내부 정적 클래스: NotificationStatusInfo
    @Getter
    @RequiredArgsConstructor
    public static class NotificationStatusInfo {
        private final NotificationStatus chatStatus;
        private final NotificationStatus scheduleStatus;
    }

    // ✅ Enum: Provider
    public enum Provider {
        ANDROID,
        IOS
    }

    // ✅ Enum: PushTarget
    public enum PushTarget {
        CHAT,
        SCHEDULE
    }
}