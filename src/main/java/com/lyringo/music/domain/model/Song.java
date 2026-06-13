package com.lyringo.music.domain.model;

import com.lyringo.music.domain.valueobject.AlbumId;
import com.lyringo.music.domain.valueobject.ArtistId;
import com.lyringo.music.domain.valueobject.AudioAssetId;
import com.lyringo.music.domain.valueobject.SongId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;

public class Song {
  private final SongId id;
  private String title;
  private final ArtistId artistId;
  private AlbumId albumId;
  private Long durationMs;
  private String language;
  private SongStatus status;
  private String coverUrl;
  private AudioAssetId audioAssetId;
  private final UserId createdBy;
  private final Instant createdAt;
  private Instant updatedAt;

  public Song(
      SongId id,
      String title,
      ArtistId artistId,
      AlbumId albumId,
      Long durationMs,
      String language,
      SongStatus status,
      String coverUrl,
      AudioAssetId audioAssetId,
      UserId createdBy,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.title = requireTitle(title);
    this.artistId = artistId;
    this.albumId = albumId;
    this.durationMs = durationMs;
    this.language = language;
    this.status = status;
    this.coverUrl = coverUrl;
    this.audioAssetId = audioAssetId;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Song create(
      String title, ArtistId artistId, AlbumId albumId, String language, UserId createdBy) {
    Instant now = Instant.now();
    return new Song(
        SongId.newId(),
        title,
        artistId,
        albumId,
        null,
        language,
        SongStatus.DRAFT,
        null,
        null,
        createdBy,
        now,
        now);
  }

  public void markAsUploading() {
    if (status != SongStatus.DRAFT) {
      throw new IllegalStateException("Only draft songs can be marked as uploading");
    }
    status = SongStatus.UPLOADING;
    updatedAt = Instant.now();
  }

  public void attachAudioAsset(AudioAssetId assetId, long durationMs) {
    this.audioAssetId = assetId;
    this.durationMs = durationMs;
    this.status = SongStatus.READY;
    this.updatedAt = Instant.now();
  }

  public void archive() {
    this.status = SongStatus.ARCHIVED;
    this.updatedAt = Instant.now();
  }

  public boolean canBePlayed() {
    return status == SongStatus.READY && audioAssetId != null;
  }

  private String requireTitle(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Song title must not be blank");
    }
    return value.trim();
  }

  public SongId id() {
    return id;
  }

  public String title() {
    return title;
  }

  public ArtistId artistId() {
    return artistId;
  }

  public AlbumId albumId() {
    return albumId;
  }

  public Long durationMs() {
    return durationMs;
  }

  public String language() {
    return language;
  }

  public SongStatus status() {
    return status;
  }

  public String coverUrl() {
    return coverUrl;
  }

  public AudioAssetId audioAssetId() {
    return audioAssetId;
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
