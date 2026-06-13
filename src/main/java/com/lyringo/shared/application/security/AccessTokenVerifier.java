package com.lyringo.shared.application.security;

public interface AccessTokenVerifier {

  AccessTokenPayload verify(String accessToken);
}
