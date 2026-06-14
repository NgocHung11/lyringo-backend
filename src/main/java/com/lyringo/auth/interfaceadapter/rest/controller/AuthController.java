package com.lyringo.auth.interfaceadapter.rest.controller;

import com.lyringo.auth.application.command.LoginCommand;
import com.lyringo.auth.application.command.RefreshTokenCommand;
import com.lyringo.auth.application.command.RegisterCommand;
import com.lyringo.auth.application.result.AuthResult;
import com.lyringo.auth.domain.exception.InvalidRefreshTokenException;
import com.lyringo.auth.infrastructure.service.TransactionalAuthService;
import com.lyringo.auth.interfaceadapter.rest.request.LoginRequest;
import com.lyringo.auth.interfaceadapter.rest.request.RegisterRequest;
import com.lyringo.auth.interfaceadapter.rest.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

  private final TransactionalAuthService authService;
  private final boolean secureCookie;

  public AuthController(
      TransactionalAuthService authService,
      @Value("${lyringo.security.cookies.secure:false}") boolean secureCookie) {
    this.authService = authService;
    this.secureCookie = secureCookie;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
      @Valid @RequestBody RegisterRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    AuthResult result =
        authService.register(
            new RegisterCommand(
                request.email(),
                request.username(),
                request.displayName(),
                request.password(),
                userAgent(httpRequest),
                clientIp(httpRequest)));

    addRefreshTokenCookie(httpResponse, result.refreshToken());

    return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.from(result));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    AuthResult result =
        authService.login(
            new LoginCommand(
                request.email(),
                request.password(),
                userAgent(httpRequest),
                clientIp(httpRequest)));

    addRefreshTokenCookie(httpResponse, result.refreshToken());

    return ResponseEntity.ok(AuthResponse.from(result));
  }

  private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    ResponseCookie cookie =
        ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite("Lax")
            .path("/api/v1/auth")
            .maxAge(Duration.ofDays(30))
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private String userAgent(HttpServletRequest request) {
    return request.getHeader(HttpHeaders.USER_AGENT);
  }

  private String clientIp(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");

    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor.split(",")[0].trim();
    }

    return request.getRemoteAddr();
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
      @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse httpResponse) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new InvalidRefreshTokenException();
    }

    AuthResult result = authService.refresh(new RefreshTokenCommand(refreshToken));

    addRefreshTokenCookie(httpResponse, result.refreshToken());

    return ResponseEntity.ok(AuthResponse.from(result));
  }
}
