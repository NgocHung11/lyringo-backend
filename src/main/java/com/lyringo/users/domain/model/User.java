package com.lyringo.users.domain.model;

import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.domain.valueobject.Username;
import java.time.Instant;

public class User {
  private final UserId id;
  private final Email email;
  private final Username username;
  private String displayName;
  private String avatarUrl;
  private UserStatus status;
  private final UserRole role;
  private final Instant createdAt;
  private Instant updatedAt;

  public User(
      UserId id,
      Email email,
      Username username,
      String displayName,
      String avatarUrl,
      UserStatus status,
      UserRole role,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.displayName = requireDisplayName(displayName);
    this.avatarUrl = avatarUrl;
    this.status = status;
    this.role = role;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static User create(Email email, Username username, String displayName) {
    Instant now = Instant.now();
    return new User(
        UserId.newId(),
        email,
        username,
        displayName,
        null,
        UserStatus.ACTIVE,
        UserRole.USER,
        now,
        now);
  }

  public void changeDisplayName(String newDisplayName) {
    this.displayName = requireDisplayName(newDisplayName);
    this.updatedAt = Instant.now();
  }

  public void changeAvatar(String newAvatarUrl) {
    this.avatarUrl = newAvatarUrl;
    this.updatedAt = Instant.now();
  }

  public void deactivate() {
    this.status = UserStatus.INACTIVE;
    this.updatedAt = Instant.now();
  }

  private String requireDisplayName(String value) {
    if (value == null || value.isBlank() || value.trim().length() < 2) {
      throw new IllegalArgumentException("Display name must contain at least 2 characters");
    }
    return value.trim();
  }

  public UserId id() {
    return id;
  }

  public Email email() {
    return email;
  }

  public Username username() {
    return username;
  }

  public String displayName() {
    return displayName;
  }

  public String avatarUrl() {
    return avatarUrl;
  }

  public UserStatus status() {
    return status;
  }

  public UserRole role() {
    return role;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
