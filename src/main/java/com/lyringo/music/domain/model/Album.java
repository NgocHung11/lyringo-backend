package com.lyringo.music.domain.model;

import com.lyringo.music.domain.valueobject.AlbumId;
import com.lyringo.music.domain.valueobject.ArtistId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;
import java.time.LocalDate;

public class Album {
  private final AlbumId id;
  private String title;
  private final ArtistId artistId;
  private String coverUrl;
  private LocalDate releaseDate;
  private final UserId createdBy;
  private final Instant createdAt;
  private Instant updatedAt;

  public Album(
      AlbumId id,
      String title,
      ArtistId artistId,
      String coverUrl,
      LocalDate releaseDate,
      UserId createdBy,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.title = requireTitle(title);
    this.artistId = artistId;
    this.coverUrl = coverUrl;
    this.releaseDate = releaseDate;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Album create(
      String title, ArtistId artistId, String coverUrl, LocalDate releaseDate, UserId createdBy) {
    Instant now = Instant.now();
    return new Album(AlbumId.newId(), title, artistId, coverUrl, releaseDate, createdBy, now, now);
  }

  private String requireTitle(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Album title must not be blank");
    }
    return value.trim();
  }

  public AlbumId id() {
    return id;
  }

  public String title() {
    return title;
  }

  public ArtistId artistId() {
    return artistId;
  }

  public String coverUrl() {
    return coverUrl;
  }

  public LocalDate releaseDate() {
    return releaseDate;
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
