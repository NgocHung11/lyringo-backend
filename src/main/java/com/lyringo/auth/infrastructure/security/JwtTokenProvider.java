package com.lyringo.auth.infrastructure.security;

import com.lyringo.auth.application.port.TokenPair;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.application.security.AccessTokenPayload;
import com.lyringo.shared.application.security.AccessTokenVerifier;
import com.lyringo.shared.application.security.InvalidAccessTokenException;
import com.lyringo.shared.domain.valueobject.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider, AccessTokenVerifier {

  private static final int REFRESH_TOKEN_BYTES = 64;

  private final String issuer;
  private final Duration accessTokenTtl;
  private final SecretKey signingKey;
  private final SecureRandom secureRandom;

  public JwtTokenProvider(
    @Value("${lyringo.security.jwt.issuer}") String issuer,
    @Value("${lyringo.security.jwt.secret}") String secret,
    @Value("${lyringo.security.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes) {
    this.issuer = issuer;
    this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    this.signingKey = createSigningKey(secret);
    this.secureRandom = new SecureRandom();
  }

  @Override
  public TokenPair issueTokens(UserId userId, AuthSessionId sessionId) {
    String accessToken = createAccessToken(userId, sessionId);
    String refreshToken = createRefreshToken();

    return new TokenPair(accessToken, refreshToken);
  }

  @Override
  public AccessTokenPayload verify(String accessToken) {
    try {
      Claims claims =
        Jwts.parser()
          .verifyWith(signingKey)
          .requireIssuer(issuer)
          .build()
          .parseSignedClaims(accessToken)
          .getPayload();

      UUID userId = UUID.fromString(claims.getSubject());
      String sessionId = claims.get("sid", String.class);

      if (sessionId == null || sessionId.isBlank()) {
        throw new InvalidAccessTokenException();
      }

      return new AccessTokenPayload(
        userId,
        UUID.fromString(sessionId),
        claims.getId(),
        claims.getExpiration().toInstant());
    } catch (JwtException | IllegalArgumentException exception) {
      throw new InvalidAccessTokenException();
    }
  }

  private String createAccessToken(UserId userId, AuthSessionId sessionId) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(accessTokenTtl);

    return Jwts.builder()
      .issuer(issuer)
      .subject(userId.value().toString())
      .id(UUID.randomUUID().toString())
      .issuedAt(Date.from(now))
      .expiration(Date.from(expiresAt))
      .claim("sid", sessionId.value().toString())
      .signWith(signingKey)
      .compact();
  }

  private String createRefreshToken() {
    byte[] tokenBytes = new byte[REFRESH_TOKEN_BYTES];
    secureRandom.nextBytes(tokenBytes);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
  }

  private SecretKey createSigningKey(String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalArgumentException("JWT secret must not be blank");
    }

    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

    if (secretBytes.length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
    }

    return Keys.hmacShaKeyFor(secretBytes);
  }
}
