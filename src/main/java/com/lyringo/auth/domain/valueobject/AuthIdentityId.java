package com.lyringo.auth.domain.valueobject;

import java.util.UUID;

public record AuthIdentityId(UUID value) {
  public AuthIdentityId {
    if (value == null) {
      throw new IllegalArgumentException("Auth identity id must not be null");
    }
  }

  public static AuthIdentityId newId() {
    return new AuthIdentityId(UUID.randomUUID());
  }
}
