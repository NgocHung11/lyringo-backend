package com.lyringo.shared.domain.valueobject;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {
  private static final Pattern SIMPLE_EMAIL_PATTERN =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

  public Email {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Email must not be blank");
    }
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    if (!SIMPLE_EMAIL_PATTERN.matcher(normalized).matches()) {
      throw new IllegalArgumentException("Email is invalid");
    }
    value = normalized;
  }
}
