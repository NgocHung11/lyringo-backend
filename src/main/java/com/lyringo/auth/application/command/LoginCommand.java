package com.lyringo.auth.application.command;

public record LoginCommand(String email, String password, String userAgent, String ipAddress) {}
