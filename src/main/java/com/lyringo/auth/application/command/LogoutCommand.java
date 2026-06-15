package com.lyringo.auth.application.command;

import java.util.UUID;

public record LogoutCommand(String refreshToken, UUID sessionId) {}
