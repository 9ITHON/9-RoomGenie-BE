package team9.demo.implementation.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team9.demo.implementation.contact.Contact;
import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.push.PushNotificationRepository;
import team9.demo.repository.user.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAppender {

    private final UserRepository userRepository;
    private final PushNotificationRepository pushNotificationRepository;

    public UserInfo append(Contact contact, String userName) {
        return userRepository.append(contact, userName);
    }

    public void appendPassword(UserId userId, String password) {
        log.debug("appendPassword - userId: {}", userId.getId());
        userRepository.appendPassword(userId, password);

    }

    public void appendUserPushToken(UserInfo userInfo, String appToken, PushInfo.Device device) {
        pushNotificationRepository.append(device, appToken, userInfo);
    }
}