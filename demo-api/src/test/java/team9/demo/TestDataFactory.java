package team9.demo;

import org.springframework.mock.web.MockMultipartFile;
import team9.demo.model.auth.JwtToken;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static byte[] createTestPngBytes() {
        try {
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MockMultipartFile createTestImageFile(String paramName, String filename) {
        return new MockMultipartFile(paramName, filename, "image/png", createTestPngBytes());
    }

    public static JwtToken createJwtToken() {
        return JwtToken.of("accessToken", RefreshToken.of("refreshToken", LocalDateTime.now()));
    }

    public static UserId createUserId() {
        return UserId.of("userId");
    }



}