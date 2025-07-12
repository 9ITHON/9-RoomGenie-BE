package team9.demo.model.token;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshToken {

    private final String token;
    private final LocalDateTime expiredAt;

    private RefreshToken(String token, LocalDateTime expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public static RefreshToken of(String token, LocalDateTime expiredAt) {
        return new RefreshToken(token, expiredAt);
    }
}