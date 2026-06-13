package com.lyringo.users.infrastructure.persistence.jpa;

import com.lyringo.users.domain.model.UserRole;
import com.lyringo.users.domain.model.UserStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "username", nullable = false, unique = true, length = 30)
  private String username;

  @Column(name = "display_name", nullable = false, length = 120)
  private String displayName;

  @Column(name = "avatar_url")
  private String avatarUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 30)
  private UserStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 30)
  private UserRole role;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected UserJpaEntity() {}

  public UserJpaEntity(
    UUID id,
    String email,
    String username,
    String displayName,
    String avatarUrl,
    UserStatus status,
    UserRole role,
    Instant createdAt,
    Instant updatedAt) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.displayName = displayName;
    this.avatarUrl = avatarUrl;
    this.status = status;
    this.role = role;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getUsername() {
    return username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public UserStatus getStatus() {
    return status;
  }

  public UserRole getRole() {
    return role;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
