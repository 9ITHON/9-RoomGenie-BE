package team9.demo.util.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team9.demo.error.AuthorizationException;
import team9.demo.error.ErrorCode;
import team9.demo.model.auth.JwtToken;
import team9.demo.model.token.RefreshToken;
import team9.demo.model.user.UserId;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtTokenUtil(
            @Value("${jwt.secret}") String secretKeyString,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public JwtToken createJwtToken(UserId userId) {
        String accessToken = createAccessToken(userId);
        RefreshToken refreshToken = createRefreshToken(userId);
        return JwtToken.of(accessToken, refreshToken);
    }

    public String createAccessToken(UserId userId) {
        Claims claims = Jwts.claims().setSubject(userId.getId());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public RefreshToken createRefreshToken(UserId userId) {
        Claims claims = Jwts.claims().setSubject(userId.getId());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return RefreshToken.of(token, LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
    }

    public void validateToken(String token) {
        try {
            getClaimsFromToken(token);
        } catch (ExpiredJwtException e) {
            throw new AuthorizationException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthorizationException(ErrorCode.INVALID_TOKEN);
        }
    }

    public void validateRefreshToken(String refreshToken) {
        String cleaned = cleanedToken(refreshToken);
        validateToken(cleaned);
    }

    public UserId getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(cleanedToken(token));
        return UserId.of(claims.getSubject());
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String cleanedToken(String token) {
        return token.replaceFirst("(?i)^Bearer ", "").trim();
    }

    public Pair<JwtToken, UserId> refresh(String token) {
        String cleaned = cleanedToken(token);
        validateRefreshToken(cleaned);
        UserId userId = getUserIdFromToken(cleaned);
        return Pair.of(createJwtToken(userId), userId);
    }
}