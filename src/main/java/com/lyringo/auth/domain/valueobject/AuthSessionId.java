package com.lyringo.auth.domain.valueobject;

import java.util.UUID;

public record AuthSessionId(UUID value) {
  public AuthSessionId {
    if (value == null) {
      throw new IllegalArgumentException("Auth session id must not be null");
    }
  }

  public static AuthSessionId newId() {
    return new AuthSessionId(UUID.randomUUID());
  }
}
