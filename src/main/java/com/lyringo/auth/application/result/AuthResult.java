package com.lyringo.auth.application.result;

import com.lyringo.auth.application.dto.AuthenticatedUserDto;

public record AuthResult(AuthenticatedUserDto user, String accessToken, String refreshToken) {}
