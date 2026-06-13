package com.lyringo.auth.interfaceadapter.rest.response;

import com.lyringo.auth.application.result.AuthResult;

public record AuthResponse(AuthenticatedUserResponse user, String accessToken) {

  public static AuthResponse from(AuthResult result) {
    return new AuthResponse(AuthenticatedUserResponse.from(result.user()), result.accessToken());
  }
}
