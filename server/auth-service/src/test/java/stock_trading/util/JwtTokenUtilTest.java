package stock_trading.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import stock_trading.entity.UserPrincipal;

import javax.crypto.SecretKey;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    @Mock
    private Authentication authentication;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    private final String testSecret = "testSecretKeyWhichIsLongEnoughForHS512Algorithm";
    private final long testExpiration = 3600000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenUtil, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenUtil, "jwtExpiration", testExpiration);

        when(userPrincipal.getEmail()).thenReturn("test@example.com");
        when(userPrincipal.getId()).thenReturn(123L);

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
    }


    @Test
    @DisplayName("Should generate token with correct expiration")
    void shouldGenerateTokenWithCorrectExpiration() {
        System.currentTimeMillis();

        String token = jwtTokenUtil.generateToken(authentication);

        var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        long expectedExpirationTime = issuedAt.getTime() + testExpiration;
        long actualExpirationTime = expiration.getTime();

        assertThat(actualExpirationTime).isBetween(
                expectedExpirationTime - 1000,
                expectedExpirationTime + 1000
        );
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        String token = jwtTokenUtil.generateToken(authentication);

        boolean isValid = jwtTokenUtil.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate expired token")
    void shouldInvalidateExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtTokenUtil, "jwtExpiration", 1L); // 1 ms

        String token = jwtTokenUtil.generateToken(authentication);

        Thread.sleep(10);

        boolean isValid = jwtTokenUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate token with invalid signature")
    void shouldInvalidateTokenWithInvalidSignature() {
        jwtTokenUtil.generateToken(authentication);

        JwtTokenUtil differentUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(differentUtil, "jwtSecret", "differentSecretKeyWhichIsAlsoLongEnough");
        ReflectionTestUtils.setField(differentUtil, "jwtExpiration", testExpiration);
        String differentToken = differentUtil.generateToken(authentication);

        boolean isValid = jwtTokenUtil.validateToken(differentToken);

        assertThat(isValid).isFalse();
    }


    @Test
    @DisplayName("Should extract email from valid token")
    void shouldExtractEmailFromValidToken() {
        String token = jwtTokenUtil.generateToken(authentication);

        String email = jwtTokenUtil.getEmailFromToken(token);

        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        UserPrincipal user2 = mock(UserPrincipal.class);
        when(user2.getEmail()).thenReturn("user2@example.com");
        when(user2.getId()).thenReturn(789L);

        Authentication auth2 = mock(Authentication.class);
        when(auth2.getPrincipal()).thenReturn(user2);

        String token1 = jwtTokenUtil.generateToken(authentication);
        String token2 = jwtTokenUtil.generateToken(auth2);

        assertThat(token1).isNotEqualTo(token2);

        String email1 = jwtTokenUtil.getEmailFromToken(token1);
        String email2 = jwtTokenUtil.getEmailFromToken(token2);

        assertThat(email1).isEqualTo("test@example.com");
        assertThat(email2).isEqualTo("user2@example.com");
    }

    @Test
    @DisplayName("Should handle very long expiration times")
    void shouldHandleVeryLongExpirationTimes() {
        // Given
        ReflectionTestUtils.setField(jwtTokenUtil, "jwtExpiration", 365L * 24 * 60 * 60 * 1000); // 1 year

        String token = jwtTokenUtil.generateToken(authentication);

        assertThat(token).isNotNull();
        boolean isValid = jwtTokenUtil.validateToken(token);
        assertThat(isValid).isTrue();

        var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getExpiration()).isAfter(new Date());
    }


    @Test
    @DisplayName("Should generate token with correct issued at time")
    void shouldGenerateTokenWithCorrectIssuedAtTime() {
        long beforeGeneration = System.currentTimeMillis();

        String token = jwtTokenUtil.generateToken(authentication);

        long afterGeneration = System.currentTimeMillis();

        var claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        assertThat(issuedAt).isNotNull();
        assertThat(issuedAt.getTime()).isBetween(beforeGeneration - 1000, afterGeneration + 1000);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(testSecret.getBytes());
    }

}