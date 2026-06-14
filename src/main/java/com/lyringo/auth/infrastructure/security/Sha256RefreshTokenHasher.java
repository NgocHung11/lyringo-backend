package com.lyringo.auth.infrastructure.security;

import com.lyringo.auth.application.port.RefreshTokenHasher;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class Sha256RefreshTokenHasher implements RefreshTokenHasher {

  @Override
  public String hash(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new IllegalArgumentException("Refresh token must not be blank");
    }

    return sha256(refreshToken);
  }

  @Override
  public boolean matches(String refreshToken, String refreshTokenHash) {
    if (refreshToken == null || refreshTokenHash == null || refreshTokenHash.isBlank()) {
      return false;
    }

    return MessageDigest.isEqual(
        sha256(refreshToken).getBytes(StandardCharsets.UTF_8),
        refreshTokenHash.getBytes(StandardCharsets.UTF_8));
  }

  private String sha256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 algorithm is not available", exception);
    }
  }
}
