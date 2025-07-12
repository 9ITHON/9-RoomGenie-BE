package team9.demo.implementation.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import team9.demo.error.AuthorizationException;
import team9.demo.error.ErrorCode;

@Component
public class AuthValidator {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void validateVerifyCode(String existsVerificationCode, String verificationCode) {
        if (!existsVerificationCode.equals(verificationCode)) {
            throw new AuthorizationException(ErrorCode.WRONG_VERIFICATION_CODE);
        }
    }

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }
    }
}
