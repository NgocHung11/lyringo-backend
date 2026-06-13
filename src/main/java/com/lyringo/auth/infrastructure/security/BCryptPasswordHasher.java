package com.lyringo.auth.infrastructure.security;

import com.lyringo.auth.application.port.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

  private final PasswordEncoder passwordEncoder;

  public BCryptPasswordHasher() {
    this.passwordEncoder = new BCryptPasswordEncoder(12);
  }

  @Override
  public String hash(String rawPassword) {
    if (rawPassword == null || rawPassword.isBlank()) {
      throw new IllegalArgumentException("Password must not be blank");
    }

    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String passwordHash) {
    if (rawPassword == null || passwordHash == null || passwordHash.isBlank()) {
      return false;
    }

    return passwordEncoder.matches(rawPassword, passwordHash);
  }
}
