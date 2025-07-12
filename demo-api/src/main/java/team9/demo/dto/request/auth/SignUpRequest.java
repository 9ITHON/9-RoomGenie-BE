package team9.demo.dto.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.contact.LocalPhoneNumber;
import team9.demo.model.notification.PushInfo;
public class SignUpRequest {

    @Getter
    @NoArgsConstructor
    public static class Phone {
        private String userName;
        private String phoneNumber;
        private String countryCode;
        private String verificationCode;
        private String deviceId;
        private String provider;
        private String appToken;

        // ✅ 전체 필드 생성자 추가
        public Phone(String phoneNumber, String countryCode, String verificationCode,
                     String deviceId, String provider, String appToken, String userName) {
            this.phoneNumber = phoneNumber;
            this.countryCode = countryCode;
            this.verificationCode = verificationCode;
            this.deviceId = deviceId;
            this.provider = provider;
            this.appToken = appToken;
            this.userName = userName;
        }

        public PushInfo.Device toDevice() {
            return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.toUpperCase()));
        }

        public String toAppToken() {
            return appToken;
        }

        public String toVerificationCode() {
            return verificationCode;
        }

        public LocalPhoneNumber toLocalPhoneNumber() {
            return LocalPhoneNumber.of(phoneNumber, countryCode);
        }

        public String toUserName() {
            return userName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Password {
        private String password = "";

        public Password(String password) {
            this.password = password;
        }
    }


}