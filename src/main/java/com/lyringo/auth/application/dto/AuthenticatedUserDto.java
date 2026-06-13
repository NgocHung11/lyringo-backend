package com.lyringo.auth.application.dto;

public record AuthenticatedUserDto(
    String id, String email, String username, String displayName, String avatarUrl, String role) {}
