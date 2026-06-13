package com.lyringo.music.domain.model;

import com.lyringo.music.domain.valueobject.AudioAssetId;
import com.lyringo.music.domain.valueobject.SongId;
import java.time.Instant;

public class AudioAsset {
  private final AudioAssetId id;
  private final SongId songId;
  private final String storageKey;
  private final String originalFileName;
  private final String mimeType;
  private final String format;
  private final Integer bitrate;
  private final long fileSize;
  private final long durationMs;
  private final String checksum;
  private final Instant createdAt;

  public AudioAsset(
      AudioAssetId id,
      SongId songId,
      String storageKey,
      String originalFileName,
      String mimeType,
      String format,
      Integer bitrate,
      long fileSize,
      long durationMs,
      String checksum,
      Instant createdAt) {
    this.id = id;
    this.songId = songId;
    this.storageKey = storageKey;
    this.originalFileName = originalFileName;
    this.mimeType = mimeType;
    this.format = format;
    this.bitrate = bitrate;
    this.fileSize = fileSize;
    this.durationMs = durationMs;
    this.checksum = checksum;
    this.createdAt = createdAt;
  }

  public static AudioAsset create(
      SongId songId,
      String storageKey,
      String originalFileName,
      String mimeType,
      String format,
      Integer bitrate,
      long fileSize,
      long durationMs,
      String checksum) {
    return new AudioAsset(
        AudioAssetId.newId(),
        songId,
        storageKey,
        originalFileName,
        mimeType,
        format,
        bitrate,
        fileSize,
        durationMs,
        checksum,
        Instant.now());
  }

  public AudioAssetId id() {
    return id;
  }

  public SongId songId() {
    return songId;
  }

  public String storageKey() {
    return storageKey;
  }

  public long durationMs() {
    return durationMs;
  }
}
