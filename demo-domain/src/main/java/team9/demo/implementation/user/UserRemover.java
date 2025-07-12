package team9.demo.implementation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ErrorCode;
import team9.demo.error.NotFoundException;
import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.push.PushNotificationRepository;
import team9.demo.repository.user.UserRepository;

@Component
@RequiredArgsConstructor
public class UserRemover {

    private final UserRepository userRepository;
    private final PushNotificationRepository pushNotificationRepository;

    public UserInfo remove(UserId userId) {
        UserInfo user = userRepository.remove(userId);
        if (user == null) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public void removePushToken(PushInfo.Device device) {
        pushNotificationRepository.remove(device);
    }
}