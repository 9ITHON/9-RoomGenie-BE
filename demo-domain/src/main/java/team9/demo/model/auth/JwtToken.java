package team9.demo.model.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team9.demo.model.token.RefreshToken;

@Getter
@RequiredArgsConstructor
public class JwtToken {

    private final String accessToken;
    private final RefreshToken refreshToken;


    public static JwtToken of(String accessToken, RefreshToken refreshToken) {
        return new JwtToken(accessToken, refreshToken);
    }


}

