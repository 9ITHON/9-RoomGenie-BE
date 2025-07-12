package team9.demo.external;

import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.model.contact.LocalPhoneNumber;

public interface ExternalAuthClient {
    void cacheVerificationCode(PhoneNumber phoneNumber, String verificationCode);
    void sendSms(LocalPhoneNumber localPhoneNumber, String verificationCode);
    String readVerificationCode(PhoneNumber phoneNumber);
    void deleteVerificationCode(PhoneNumber phoneNumber);
}
