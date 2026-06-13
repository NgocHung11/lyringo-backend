package com.lyringo.music.application.port;

public record SignedStorageUrl(String url, long expiresInSeconds) {}
