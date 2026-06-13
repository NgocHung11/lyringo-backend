package com.lyringo.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BCryptPasswordHasherTest {

  private final BCryptPasswordHasher passwordHasher = new BCryptPasswordHasher();

  @Test
  void shouldHashPasswordAndMatchRawPassword() {
    String rawPassword = "StrongPassword123!";

    String passwordHash = passwordHasher.hash(rawPassword);

    assertThat(passwordHash).isNotEqualTo(rawPassword);
    assertThat(passwordHasher.matches(rawPassword, passwordHash)).isTrue();
  }

  @Test
  void shouldNotMatchWrongPassword() {
    String passwordHash = passwordHasher.hash("StrongPassword123!");

    boolean matched = passwordHasher.matches("WrongPassword123!", passwordHash);

    assertThat(matched).isFalse();
  }

  @Test
  void shouldGenerateDifferentHashesForSamePassword() {
    String rawPassword = "StrongPassword123!";

    String hash1 = passwordHasher.hash(rawPassword);
    String hash2 = passwordHasher.hash(rawPassword);

    assertThat(hash1).isNotEqualTo(hash2);
    assertThat(passwordHasher.matches(rawPassword, hash1)).isTrue();
    assertThat(passwordHasher.matches(rawPassword, hash2)).isTrue();
  }
}
