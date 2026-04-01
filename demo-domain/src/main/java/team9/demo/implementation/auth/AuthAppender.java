package team9.demo.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.external.ExternalAuthClient;
import team9.demo.implementation.contact.Contact;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;
import team9.demo.repository.auth.LoggedInRepository;

@Component
@RequiredArgsConstructor
public class AuthAppender {

    private final LoggedInRepository loggedInRepository;
    private final ExternalAuthClient externalAuthClient;

    public void appendLoggedIn(RefreshToken newRefreshToken, UserId userId) {
        loggedInRepository.append(newRefreshToken, userId);
    }

    public void appendVerification(Contact contact, String verificationCode) {
        if (contact instanceof PhoneNumber phoneNumber) {
            externalAuthClient.cacheVerificationCode(phoneNumber, verificationCode);
        } else {
            throw new ConflictException(ErrorCode.NOT_SUPPORT_CONTACT_TYPE);
        }
    }
}