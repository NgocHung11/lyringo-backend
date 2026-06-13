package com.lyringo.auth.domain.model;

import com.lyringo.auth.domain.valueobject.AuthIdentityId;
import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;

public class AuthIdentity {
  private final AuthIdentityId id;
  private final UserId userId;
  private final AuthProvider provider;
  private final String providerUserId;
  private final Email email;
  private final String passwordHash;
  private final Instant createdAt;
  private Instant updatedAt;

  public AuthIdentity(
      AuthIdentityId id,
      UserId userId,
      AuthProvider provider,
      String providerUserId,
      Email email,
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

  public static AuthIdentity emailPassword(UserId userId, Email email, String passwordHash) {
    Instant now = Instant.now();
    return new AuthIdentity(
        AuthIdentityId.newId(),
        userId,
        AuthProvider.EMAIL_PASSWORD,
        email.value(),
        email,
        passwordHash,
        now,
        now);
  }

  public AuthIdentityId id() {
    return id;
  }

  public UserId userId() {
    return userId;
  }

  public AuthProvider provider() {
    return provider;
  }

  public String providerUserId() {
    return providerUserId;
  }

  public Email email() {
    return email;
  }

  public String passwordHash() {
    return passwordHash;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
