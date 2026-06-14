package com.lyringo.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.lyringo.auth.application.port.TokenPair;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.application.security.AccessTokenPayload;
import com.lyringo.shared.domain.valueobject.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static final String SECRET = "test-lyringo-jwt-secret-minimum-32-bytes-1234567890";

  private final JwtTokenProvider tokenProvider = new JwtTokenProvider("lyringo-test", SECRET, 15);

  @Test
  void shouldIssueJwtAccessTokenAndOpaqueRefreshToken() {
    UserId userId = UserId.newId();
    AuthSessionId sessionId = AuthSessionId.newId();

    TokenPair tokenPair = tokenProvider.issueTokens(userId, sessionId);

    assertThat(tokenPair.accessToken()).isNotBlank();
    assertThat(tokenPair.refreshToken()).isNotBlank();
    assertThat(tokenPair.refreshToken()).doesNotContain(".");
  }

  @Test
  void shouldPutUserIdAndSessionIdIntoAccessToken() {
    UserId userId = UserId.newId();
    AuthSessionId sessionId = AuthSessionId.newId();

    TokenPair tokenPair = tokenProvider.issueTokens(userId, sessionId);

    Claims claims = parseClaims(tokenPair.accessToken());

    assertThat(claims.getSubject()).isEqualTo(userId.value().toString());
    assertThat(claims.getIssuer()).isEqualTo("lyringo-test");
    assertThat(claims.get("sid", String.class)).isEqualTo(sessionId.value().toString());
    assertThat(claims.getExpiration()).isNotNull();
  }

  @Test
  void shouldGenerateDifferentRefreshTokensEveryTime() {
    UserId userId = UserId.newId();
    AuthSessionId sessionId = AuthSessionId.newId();

    TokenPair first = tokenProvider.issueTokens(userId, sessionId);
    TokenPair second = tokenProvider.issueTokens(userId, sessionId);

    assertThat(first.refreshToken()).isNotEqualTo(second.refreshToken());
    assertThat(first.accessToken()).isNotEqualTo(second.accessToken());
  }

  private Claims parseClaims(String accessToken) {
    SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();
  }

  @Test
  void shouldVerifyAccessToken() {
    UserId userId = UserId.newId();
    AuthSessionId sessionId = AuthSessionId.newId();

    TokenPair tokenPair = tokenProvider.issueTokens(userId, sessionId);

    AccessTokenPayload payload = tokenProvider.verify(tokenPair.accessToken());

    assertThat(payload.userId()).isEqualTo(userId.value());
    assertThat(payload.sessionId()).isEqualTo(sessionId.value());
    assertThat(payload.tokenId()).isNotBlank();
    assertThat(payload.expiresAt()).isNotNull();
  }
}
