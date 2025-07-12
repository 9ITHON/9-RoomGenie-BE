package team9.demo.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.external.ExternalAuthClient;
import team9.demo.model.contact.LocalPhoneNumber;

@Component
@RequiredArgsConstructor
public class AuthSender {

    private final ExternalAuthClient externalAuthClient;

    public void sendVerificationCode(LocalPhoneNumber localPhoneNumber, String verificationCode) {
        externalAuthClient.sendSms(localPhoneNumber, verificationCode);
    }
}