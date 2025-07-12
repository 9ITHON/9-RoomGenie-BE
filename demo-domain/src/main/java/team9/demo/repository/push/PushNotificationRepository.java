package team9.demo.repository.push;

import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.UserInfo;

public interface PushNotificationRepository {
    void remove(PushInfo.Device device);
    void append(PushInfo.Device device, String appToken, UserInfo userInfo);
}
