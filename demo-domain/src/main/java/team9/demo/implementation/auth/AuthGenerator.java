package team9.demo.implementation.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AuthGenerator {

    private final Random random = new Random();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 인증번호 6자리 생성 (현재는 디버깅용으로 "000000" 반환)
     */
    public String generateVerificationCode() {
        // 실제 랜덤 인증번호 생성: return String.valueOf(100000 + random.nextInt(900000));
        return "000000";
    }

    /**
     * 비밀번호를 Bcrypt 방식으로 해시
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
