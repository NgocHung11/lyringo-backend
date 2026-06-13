package com.lyringo.music.domain.model;

import com.lyringo.music.domain.valueobject.ArtistId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;

public class Artist {
  private final ArtistId id;
  private String name;
  private String imageUrl;
  private final UserId createdBy;
  private final Instant createdAt;
  private Instant updatedAt;

  public Artist(
      ArtistId id,
      String name,
      String imageUrl,
      UserId createdBy,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = requireName(name);
    this.imageUrl = imageUrl;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Artist create(String name, String imageUrl, UserId createdBy) {
    Instant now = Instant.now();
    return new Artist(ArtistId.newId(), name, imageUrl, createdBy, now, now);
  }

  public void rename(String newName) {
    this.name = requireName(newName);
    this.updatedAt = Instant.now();
  }

  private String requireName(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Artist name must not be blank");
    }
    return value.trim();
  }

  public ArtistId id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String imageUrl() {
    return imageUrl;
  }

  public UserId createdBy() {
    return createdBy;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
