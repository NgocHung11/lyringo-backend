package com.lyringo.music.domain.valueobject;

import java.util.UUID;

public record AlbumId(UUID value) {
  public AlbumId {
    if (value == null) {
      throw new IllegalArgumentException("Album id must not be null");
    }
  }

  public static AlbumId newId() {
    return new AlbumId(UUID.randomUUID());
  }
}
