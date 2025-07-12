package team9.demo.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team9.demo.implementation.contact.ContactFormatter;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.implementation.media.FileHandler;
import team9.demo.implementation.user.*;
import team9.demo.model.auth.CredentialTarget;
import team9.demo.model.contact.LocalPhoneNumber;
import team9.demo.model.media.FileCategory;
import team9.demo.model.media.FileData;
import team9.demo.model.media.Media;
import team9.demo.model.mission.TodayMissionInfo;
import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.User;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserValidator userValidator;
    private final ContactFormatter contactFormatter;
    private final FileHandler fileHandler;
    private final UserAppender userAppender;
    private final UserRemover userRemover;
    private final UserReader userReader;

    public Media uploadFile(FileData file, UserId userId, FileCategory category) {
        return fileHandler.handleNewFile(userId, file, category);
    }

    public void checkAvailability(LocalPhoneNumber localPhoneNumber, CredentialTarget type) {
        PhoneNumber phoneNumber = contactFormatter.formatContact(localPhoneNumber);

        switch (type) {
            case SIGN_UP -> userValidator.isNotAlreadyCreated(phoneNumber);
            case RESET -> userValidator.isAlreadyCreated(phoneNumber);
        }
    }

    public UserInfo createUser(
            LocalPhoneNumber localPhoneNumber,
            String appToken,
            PushInfo.Device device,
            String userName
    ) {
        PhoneNumber phoneNumber = contactFormatter.formatContact(localPhoneNumber);
        userValidator.isNotAlreadyCreated(phoneNumber);
        UserInfo user = userAppender.append(phoneNumber, userName);
        userRemover.removePushToken(device);
        userAppender.appendUserPushToken(user, appToken, device);
        return user;
    }


    public void createPassword(UserId userId, String password) {
        userAppender.appendPassword(userId, password);
    }

    public User getUserByContact(LocalPhoneNumber localPhoneNumber, AccessStatus accessStatus) {
        PhoneNumber phoneNumber = contactFormatter.formatContact(localPhoneNumber);
        UserInfo userInfo = userReader.readByContact(phoneNumber, accessStatus);
        return User.of(userInfo, localPhoneNumber);
    }

    public void createDeviceInfo(UserInfo userInfo, PushInfo.Device device, String appToken) {
        userAppender.appendUserPushToken(userInfo, appToken, device);
    }


}
