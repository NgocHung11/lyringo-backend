package com.lyringo.auth.application.port;

public record CreatedUser(
    String id, String email, String username, String displayName, String avatarUrl, String role) {}
