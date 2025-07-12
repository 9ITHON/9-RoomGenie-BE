package team9.demo.repository.auth;

import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;

import java.util.Optional;

public interface LoggedInRepository {
    void remove(String refreshToken);
    void append(RefreshToken refreshToken, UserId userId);
    void update(RefreshToken newRefreshToken, RefreshToken preRefreshToken);
    RefreshToken read(String refreshToken, UserId userId);  // ✅ 반환 타입 확인
}
