package team9.demo.service.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team9.demo.implementation.auth.*;
import team9.demo.implementation.contact.ContactFormatter;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.model.contact.LocalPhoneNumber;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;
import team9.demo.model.user.UserInfo;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final ContactFormatter contactFormatter;
    private final AuthGenerator authGenerator;
    private final AuthAppender authAppender;
    private final AuthSender authSender;
    private final AuthReader authReader;
    private final AuthValidator authValidator;
    /**
     * 인증번호 생성 + 저장 + 전송
     */
    public void createCredential(LocalPhoneNumber localPhoneNumber) {
        PhoneNumber phoneNumber = contactFormatter.formatContact(localPhoneNumber);
        String verificationCode = authGenerator.generateVerificationCode();

        authAppender.appendVerification(phoneNumber, verificationCode);
        authSender.sendVerificationCode(localPhoneNumber, verificationCode);
    }
    public void verify(LocalPhoneNumber localPhoneNumber, String verificationCode) {
        PhoneNumber phoneNumber = contactFormatter.formatContact(localPhoneNumber);
        String existingVerificationCode = authReader.readVerificationCode(phoneNumber);
        authValidator.validateVerifyCode(existingVerificationCode, verificationCode);
    }

    public void createLoginInfo(UserId userId, RefreshToken refreshToken) {
        authAppender.appendLoggedIn(refreshToken, userId);
    }

    public String encryptPassword(String password) {
        return authGenerator.hashPassword(password);
    }

    public void validatePassword(UserInfo userInfo, String password) {
        authValidator.validatePassword(
                password, // sourcePassword
                userInfo.getPassword() // targetPassword
        );
    }

}