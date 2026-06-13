package com.lyringo.auth.interfaceadapter.rest.response;

import com.lyringo.auth.application.dto.AuthenticatedUserDto;

public record AuthenticatedUserResponse(
  String id, String email, String username, String displayName, String avatarUrl, String role) {

  public static AuthenticatedUserResponse from(AuthenticatedUserDto user) {
    return new AuthenticatedUserResponse(
      user.id(), user.email(), user.username(), user.displayName(), user.avatarUrl(), user.role());
  }
}
