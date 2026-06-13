package com.lyringo.users.domain.valueobject;

import java.util.Locale;
import java.util.regex.Pattern;

public record Username(String value) {
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9_]{3,30}$");

  public Username {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Username must not be blank");
    }
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    if (!USERNAME_PATTERN.matcher(normalized).matches()) {
      throw new IllegalArgumentException(
          "Username must contain 3-30 lowercase letters, numbers, or underscores");
    }
    value = normalized;
  }
}
