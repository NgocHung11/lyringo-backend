package com.lyringo.shared.infrastructure.security;

import java.util.UUID;

public record AuthenticatedUserPrincipal(UUID userId, UUID sessionId, String tokenId) {}
