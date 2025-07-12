package team9.demo.implementation.contact;

import lombok.Getter;

@Getter
public class PhoneNumber extends Contact {

    private final String e164PhoneNumber;

    private PhoneNumber(String e164PhoneNumber) {
        this.e164PhoneNumber = e164PhoneNumber;
    }

    public static PhoneNumber of(String phoneNumber) {
        return new PhoneNumber(phoneNumber);
    }
}