package com.lyringo.shared.application.security;

import java.time.Instant;
import java.util.UUID;

public record AccessTokenPayload(UUID userId, UUID sessionId, String tokenId, Instant expiresAt) {}
