package com.lyringo.music.domain.valueobject;

import java.util.UUID;

public record ArtistId(UUID value) {
  public ArtistId {
    if (value == null) {
      throw new IllegalArgumentException("Artist id must not be null");
    }
  }

  public static ArtistId newId() {
    return new ArtistId(UUID.randomUUID());
  }
}
