package team9.demo.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.model.auth.CredentialTarget;
import team9.demo.model.contact.LocalPhoneNumber;
import team9.demo.model.notification.PushInfo;
import team9.demo.model.user.AccessStatus;
import team9.demo.model.user.User;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;
import team9.demo.service.auth.AuthService;
import team9.demo.service.user.UserService;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AuthService authService;
    private final UserService userService;

    public void registerCredential(LocalPhoneNumber localPhoneNumber, CredentialTarget type) {
        userService.checkAvailability(localPhoneNumber, type);
        authService.createCredential(localPhoneNumber);
    }

    public UserId createUser(
            LocalPhoneNumber localPhoneNumber,
            String verificationCode,
            String appToken,
            PushInfo.Device device,
            String userName
    ) {
        authService.verify(localPhoneNumber, verificationCode);
        UserInfo user = userService.createUser(localPhoneNumber, appToken, device, userName);
        return user.getUserId();
    }


    public void createPassword(UserId userId, String rawPassword) {
        String hashedPassword = authService.encryptPassword(rawPassword);
        userService.createPassword(userId, hashedPassword);
    }

    public UserId login(
            LocalPhoneNumber localPhoneNumber,
            String password,
            PushInfo.Device device,
            String appToken
    ) {
        User user = userService.getUserByContact(localPhoneNumber, AccessStatus.ACCESS);
        authService.validatePassword(user.getInfo(), password);
        userService.createDeviceInfo(user.getInfo(), device, appToken);
        return user.getInfo().getUserId();
    }


}
