package team9.demo.dto.request.auth;

import team9.demo.model.contact.LocalPhoneNumber;

public class VerificationRequest {

    public record Phone(String phoneNumber, String countryCode) {
        public LocalPhoneNumber toLocalPhoneNumber() {
            return LocalPhoneNumber.of(phoneNumber, countryCode);
        }
    }
}
