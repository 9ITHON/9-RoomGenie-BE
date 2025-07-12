package team9.demo.implementation.contact;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team9.demo.error.ConflictException;
import team9.demo.error.ErrorCode;
import team9.demo.model.contact.LocalPhoneNumber;

@Component
@RequiredArgsConstructor
public class ContactFormatter {

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public PhoneNumber formatContact(LocalPhoneNumber localPhoneNumber) {
        try {
            String cleanedNumber = localPhoneNumber.getNumber();

            if (!cleanedNumber.startsWith("+")) {
                if (cleanedNumber.startsWith("0")) {
                    cleanedNumber = "+" + localPhoneNumber.getCountryCode() + cleanedNumber.substring(1);
                } else {
                    cleanedNumber = "+" + localPhoneNumber.getCountryCode() + cleanedNumber;
                }
            }

            var parsedNumber = phoneUtil.parse(cleanedNumber, null);
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                throw new ConflictException(ErrorCode.INVALID_PHONE_NUMBER);
            }

            String formatted = phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            return PhoneNumber.of(formatted);
        } catch (NumberParseException e) {
            throw new ConflictException(ErrorCode.INVALID_PHONE_NUMBER);
        }
    }

    public LocalPhoneNumber extractCountryCodeAndLocalNumber(PhoneNumber e164PhoneNumber) {
        try {
            var parsed = phoneUtil.parse(e164PhoneNumber.getE164PhoneNumber(), null);
            if (!phoneUtil.isValidNumber(parsed)) {
                throw new ConflictException(ErrorCode.INVALID_PHONE_NUMBER);
            }

            String countryCode = String.valueOf(parsed.getCountryCode());
            String national = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            String localNumber = national.replaceAll("\\D", "");

            return LocalPhoneNumber.of(localNumber, countryCode);
        } catch (NumberParseException e) {
            throw new ConflictException(ErrorCode.INVALID_PHONE_NUMBER);
        }
    }
}