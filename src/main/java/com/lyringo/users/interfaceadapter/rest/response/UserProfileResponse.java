package com.lyringo.users.interfaceadapter.rest.response;

import com.lyringo.users.application.dto.UserDto;

public record UserProfileResponse(
    String id,
    String email,
    String username,
    String displayName,
    String avatarUrl,
    String status,
    String role) {

  public static UserProfileResponse from(UserDto user) {
    return new UserProfileResponse(
        user.id(),
        user.email(),
        user.username(),
        user.displayName(),
        user.avatarUrl(),
        user.status(),
        user.role());
  }
}
