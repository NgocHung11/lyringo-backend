package com.lyringo.auth.application.port;

public interface RefreshTokenHasher {

  String hash(String refreshToken);
}
