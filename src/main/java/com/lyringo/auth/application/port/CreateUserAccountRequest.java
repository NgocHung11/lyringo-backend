package com.lyringo.auth.application.port;

public record CreateUserAccountRequest(String email, String username, String displayName) {}
