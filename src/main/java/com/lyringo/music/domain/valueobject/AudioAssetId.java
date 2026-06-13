package com.lyringo.music.domain.valueobject;

import java.util.UUID;

public record AudioAssetId(UUID value) {
  public AudioAssetId {
    if (value == null) {
      throw new IllegalArgumentException("Audio asset id must not be null");
    }
  }

  public static AudioAssetId newId() {
    return new AudioAssetId(UUID.randomUUID());
  }
}
