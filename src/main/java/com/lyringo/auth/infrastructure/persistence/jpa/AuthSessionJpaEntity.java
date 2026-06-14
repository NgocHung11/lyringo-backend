package com.lyringo.auth.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_sessions")
public class AuthSessionJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "refresh_token_hash")
  private String refreshTokenHash;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "ip_address", length = 64)
  private String ipAddress;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected AuthSessionJpaEntity() {}

  public AuthSessionJpaEntity(
      UUID id,
      UUID userId,
      String refreshTokenHash,
      String userAgent,
      String ipAddress,
      Instant revokedAt,
      Instant expiresAt,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.userId = userId;
    this.refreshTokenHash = refreshTokenHash;
    this.userAgent = userAgent;
    this.ipAddress = ipAddress;
    this.revokedAt = revokedAt;
    this.expiresAt = expiresAt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getRefreshTokenHash() {
    return refreshTokenHash;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
