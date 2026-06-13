package com.lyringo.shared.application.security;

public class InvalidAccessTokenException extends RuntimeException {

  public InvalidAccessTokenException() {
    super("Invalid or expired access token");
  }
}
