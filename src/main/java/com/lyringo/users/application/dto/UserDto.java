package com.lyringo.users.application.dto;

import com.lyringo.users.domain.model.User;

public record UserDto(
    String id,
    String email,
    String username,
    String displayName,
    String avatarUrl,
    String status,
    String role) {

  public static UserDto fromDomain(User user) {
    return new UserDto(
        user.id().value().toString(),
        user.email().value(),
        user.username().value(),
        user.displayName(),
        user.avatarUrl(),
        user.status().name(),
        user.role().name());
  }
}
