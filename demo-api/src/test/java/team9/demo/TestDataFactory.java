package team9.demo;

import org.springframework.mock.web.MockMultipartFile;
import team9.demo.model.auth.JwtToken;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class TestDataFactory {



    public static JwtToken createJwtToken() {
        return JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()));
    }

    public static UserId createUserId() {
        return UserId.of("userId");
    }



}