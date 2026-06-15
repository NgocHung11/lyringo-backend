package com.lyringo.auth.domain.model;

import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;

public class AuthSession {
  private final AuthSessionId id;
  private final UserId userId;
  private String refreshTokenHash;
  private String previousRefreshTokenHash;
  private final String userAgent;
  private final String ipAddress;
  private Instant revokedAt;
  private final Instant expiresAt;
  private Instant rotatedAt;
  private final Instant createdAt;
  private Instant updatedAt;

  public AuthSession(
      AuthSessionId id,
      UserId userId,
      String refreshTokenHash,
      String previousRefreshTokenHash,
      String userAgent,
      String ipAddress,
      Instant revokedAt,
      Instant expiresAt,
      Instant rotatedAt,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.userId = userId;
    this.refreshTokenHash = refreshTokenHash;
    this.previousRefreshTokenHash = previousRefreshTokenHash;
    this.userAgent = userAgent;
    this.ipAddress = ipAddress;
    this.revokedAt = revokedAt;
    this.expiresAt = expiresAt;
    this.rotatedAt = rotatedAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static AuthSession create(
      UserId userId, String userAgent, String ipAddress, Instant expiresAt) {
    Instant now = Instant.now();
    return new AuthSession(
        AuthSessionId.newId(),
        userId,
        null,
        null,
        userAgent,
        ipAddress,
        null,
        expiresAt,
        null,
        now,
        now);
  }

  public void setRefreshTokenHash(String refreshTokenHash) {
    this.refreshTokenHash = refreshTokenHash;
    this.updatedAt = Instant.now();
  }

  public void rotateTo(String newRefreshTokenHash) {
    this.previousRefreshTokenHash = this.refreshTokenHash;
    this.refreshTokenHash = newRefreshTokenHash;
    this.rotatedAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public void revoke() {
    this.revokedAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public boolean isActive(Instant now) {
    return revokedAt == null && expiresAt.isAfter(now);
  }

  public AuthSessionId id() {
    return id;
  }

  public UserId userId() {
    return userId;
  }

  public String refreshTokenHash() {
    return refreshTokenHash;
  }

  public String previousRefreshTokenHash() {
    return previousRefreshTokenHash;
  }

  public String userAgent() {
    return userAgent;
  }

  public String ipAddress() {
    return ipAddress;
  }

  public Instant revokedAt() {
    return revokedAt;
  }

  public Instant expiresAt() {
    return expiresAt;
  }

  public Instant rotatedAt() {
    return rotatedAt;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
