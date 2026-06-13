package com.lyringo.shared.domain.valueobject;

import java.util.UUID;

public record UserId(UUID value) {
  public UserId {
    if (value == null) {
      throw new IllegalArgumentException("User id must not be null");
    }
  }

  public static UserId newId() {
    return new UserId(UUID.randomUUID());
  }
}
