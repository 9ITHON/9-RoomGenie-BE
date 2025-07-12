package team9.demo.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team9.demo.client.AuthCacheClient;
import team9.demo.client.SmsClient;
import team9.demo.dto.SmsMessageDto;
import team9.demo.implementation.contact.PhoneNumber;
import team9.demo.model.contact.LocalPhoneNumber;

@Component
@RequiredArgsConstructor
public class ExternalAuthClientImpl implements ExternalAuthClient {

    private final AuthCacheClient authCacheClient;
    private final SmsClient smsClient;

    @Value("${ncp.sms.phoneNumber}")
    private String fromPhoneNumber;

    @Override
    public void cacheVerificationCode(PhoneNumber phoneNumber, String verificationCode) {
        authCacheClient.cacheVerificationCode(phoneNumber, verificationCode);
    }

    @Override
    public String readVerificationCode(PhoneNumber phoneNumber) {
        return authCacheClient.getVerificationCode(phoneNumber);
    }

    @Override
    public void deleteVerificationCode(PhoneNumber phoneNumber) {
        authCacheClient.removeVerificationCode(phoneNumber);
    }

    @Override
    public void sendSms(LocalPhoneNumber localPhoneNumber, String verificationCode) {
        SmsMessageDto dto = SmsMessageDto.from(localPhoneNumber, fromPhoneNumber, verificationCode);
        smsClient.send(dto);
    }
}