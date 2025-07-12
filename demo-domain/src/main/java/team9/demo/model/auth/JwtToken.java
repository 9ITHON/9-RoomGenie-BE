package team9.demo.model.auth;

import team9.demo.model.token.RefreshToken;

public class JwtToken {

    private final String accessToken;
    private final RefreshToken refreshToken;

    private JwtToken(String accessToken, RefreshToken refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static JwtToken of(String accessToken, RefreshToken refreshToken) {
        return new JwtToken(accessToken, refreshToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }
}

