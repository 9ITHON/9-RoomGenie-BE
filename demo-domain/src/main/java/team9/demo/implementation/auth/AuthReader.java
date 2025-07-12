package team9.demo.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.AuthorizationException;
import team9.demo.error.ErrorCode;
import team9.demo.external.ExternalAuthClient;
import team9.demo.implementation.contact.Contact;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;
import team9.demo.repository.auth.LoggedInRepository;

@Component
@RequiredArgsConstructor
public class AuthReader {

    private final LoggedInRepository loggedInRepository;
    private final ExternalAuthClient externalAuthClient;

    public String readVerificationCode(Contact contact) {
        if (contact instanceof PhoneNumber phoneNumber) {
            String code = externalAuthClient.readVerificationCode(phoneNumber);
            if (code == null) {
                throw new AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE);
            }
            return code;
        }
        throw new IllegalArgumentException("지원되지 않는 Contact 타입입니다.");
    }

    public RefreshToken readLoginInfo(String refreshToken, UserId userId) {
        RefreshToken result = loggedInRepository.read(refreshToken, userId);
        if (result == null) {
            throw new AuthorizationException(ErrorCode.INVALID_TOKEN);
        }
        return result;
    }

}
