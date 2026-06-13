package com.lyringo.auth.application.port;

public record TokenPair(String accessToken, String refreshToken) {}
