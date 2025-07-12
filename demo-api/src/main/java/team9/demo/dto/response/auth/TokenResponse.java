package team9.demo.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team9.demo.model.auth.JwtToken;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public static TokenResponse of(JwtToken token) {
        return new TokenResponse(token.getAccessToken(), token.getRefreshToken().getToken());
    }
}