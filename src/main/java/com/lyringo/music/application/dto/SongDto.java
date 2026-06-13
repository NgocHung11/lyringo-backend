package com.lyringo.music.application.dto;

import com.lyringo.music.domain.model.Song;

public record SongDto(
    String id,
    String title,
    String artistId,
    String albumId,
    Long durationMs,
    String language,
    String status,
    String coverUrl,
    String audioAssetId,
    String createdBy) {

  public static SongDto fromDomain(Song song) {
    return new SongDto(
        song.id().value().toString(),
        song.title(),
        song.artistId().value().toString(),
        song.albumId() == null ? null : song.albumId().value().toString(),
        song.durationMs(),
        song.language(),
        song.status().name(),
        song.coverUrl(),
        song.audioAssetId() == null ? null : song.audioAssetId().value().toString(),
        song.createdBy().value().toString());
  }
}
