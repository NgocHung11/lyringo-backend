package com.lyringo.music.domain.valueobject;

import java.util.UUID;

public record SongId(UUID value) {
  public SongId {
    if (value == null) {
      throw new IllegalArgumentException("Song id must not be null");
    }
  }

  public static SongId newId() {
    return new SongId(UUID.randomUUID());
  }
}
