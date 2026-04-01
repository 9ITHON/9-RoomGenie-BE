package team9.demo.dto.request.auth;

import team9.demo.model.contact.LocalPhoneNumber;
import team9.demo.model.notification.PushInfo;

import java.time.LocalDate;

public class SignUpRequest {

    public record Phone(
            String userName,
            String phoneNumber,
            String countryCode,
            String verificationCode,
            String deviceId,
            String provider,
            String appToken
    ) {
        public PushInfo.Device toDevice() {
            return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.toUpperCase()));
        }

        public String toAppToken() { return appToken; }

        public String toVerificationCode() { return verificationCode; }

        public LocalPhoneNumber toLocalPhoneNumber() {
            return LocalPhoneNumber.of(phoneNumber, countryCode);
        }

        public String toUserName() { return userName; }
    }

    public record Password(
            String password,
            LocalDate birth,
            String email
    ) {
        public String getPassword() { return password; }
        public LocalDate getBirth() { return birth; }
        public String getEmail() { return email; }
    }
}
