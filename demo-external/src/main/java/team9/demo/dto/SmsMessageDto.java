package team9.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import team9.demo.model.contact.LocalPhoneNumber;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class SmsMessageDto {

    private final String type;
    private final String contentType;
    private final String countryCode;
    private final String from;
    private final String subject;
    private final String content;
    private final List<MessageDto> messages;

    public static SmsMessageDto from(LocalPhoneNumber localPhoneNumber, String fromPhoneNumber, String verificationCode) {
        String subject = "Verification Code";
        String content = "Your verification code is " + verificationCode;

        MessageDto message = new MessageDto(
                localPhoneNumber.getNumber(),
                subject,
                content
        );

        return new SmsMessageDto(
                "SMS",
                "COMM",
                localPhoneNumber.getCountryCode(),
                fromPhoneNumber,
                subject,
                content,
                Collections.singletonList(message)
        );
    }
}