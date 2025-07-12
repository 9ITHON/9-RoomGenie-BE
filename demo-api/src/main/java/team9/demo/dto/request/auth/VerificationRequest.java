package team9.demo.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team9.demo.model.contact.LocalPhoneNumber;

/**
 * 전화번호 인증 요청 DTO
 */
public class VerificationRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Phone {

        private String phoneNumber;
        private String countryCode;

        public LocalPhoneNumber toLocalPhoneNumber() {
            return LocalPhoneNumber.of(phoneNumber, countryCode);
        }
    }
}