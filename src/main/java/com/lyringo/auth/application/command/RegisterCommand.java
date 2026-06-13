package com.lyringo.auth.application.command;

public record RegisterCommand(
    String email,
    String username,
    String displayName,
    String password,
    String userAgent,
    String ipAddress) {}
