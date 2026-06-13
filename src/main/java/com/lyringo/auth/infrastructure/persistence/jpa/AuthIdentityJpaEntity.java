package com.lyringo.auth.infrastructure.persistence.jpa;

import com.lyringo.auth.domain.model.AuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_identities")
public class AuthIdentityJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false, length = 50)
  private AuthProvider provider;

  @Column(name = "provider_user_id", nullable = false)
  private String providerUserId;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected AuthIdentityJpaEntity() {}

  public AuthIdentityJpaEntity(
    UUID id,
    UUID userId,
    AuthProvider provider,
    String providerUserId,
    String email,
    String passwordHash,
    Instant createdAt,
    Instant updatedAt) {
    this.id = id;
    this.userId = userId;
    this.provider = provider;
    this.providerUserId = providerUserId;
    this.email = email;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public AuthProvider getProvider() {
    return provider;
  }

  public String getProviderUserId() {
    return providerUserId;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
