package team9.demo.jpaentity.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "logged_in",
        schema = "RoomGenie",
        indexes = {
                @Index(name = "logged_in_idx_refresh_token", columnList = "refreshToken"),
                @Index(name = "logged_in_idx_refresh_token_user", columnList = "refreshToken, userId")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoggedInJpaEntity {

    @Id
    private String loggedInId;

    private String refreshToken;

    private String userId;

    private LocalDateTime expiredAt;

    @Builder
    public LoggedInJpaEntity(String refreshToken, String userId, LocalDateTime expiredAt) {
        this.loggedInId = UUID.randomUUID().toString();
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.expiredAt = expiredAt;
    }

    public static LoggedInJpaEntity generate(RefreshToken refreshToken, UserId userId) {
        return LoggedInJpaEntity.builder()
                .refreshToken(refreshToken.getToken())
                .userId(userId.getId())
                .expiredAt(refreshToken.getExpiredAt())
                .build();
    }

    public RefreshToken toRefreshToken() {
        return RefreshToken.of(this.refreshToken, this.expiredAt);
    }

    public void updateRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken.getToken();
        this.expiredAt = refreshToken.getExpiredAt();
    }
}
