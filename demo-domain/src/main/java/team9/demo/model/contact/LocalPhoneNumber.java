package team9.demo.model.contact;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalPhoneNumber {

    private final String number;
    private final String countryCode;

    public static LocalPhoneNumber of(String number, String countryCode) {
        return new LocalPhoneNumber(number, countryCode);
    }

    @Override
    public String toString() {
        if (number.startsWith("+")) {
            return number; // 국제번호 형식
        }
        if (number.startsWith(countryCode)) {
            return number; // 국가코드 포함됨
        }
        if (number.startsWith("0")) {
            return "+" + countryCode + number.substring(1); // 예: 0101234 -> +82101234
        }
        return "+" + countryCode + number; // 기본 케이스
    }
}