package com.lyringo.auth.application.usecase;

import com.lyringo.auth.application.command.RefreshTokenCommand;
import com.lyringo.auth.application.dto.AuthenticatedUserDto;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.CreatedUser;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.application.port.TokenPair;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.application.port.UserReader;
import com.lyringo.auth.application.result.AuthResult;
import com.lyringo.auth.domain.exception.InvalidRefreshTokenException;
import com.lyringo.auth.domain.model.AuthSession;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

public class RefreshTokenUseCase {

  private final AuthSessionRepository authSessionRepository;
  private final RefreshTokenHasher refreshTokenHasher;
  private final TokenProvider tokenProvider;
  private final UserReader userReader;
  private final Clock clock;

  public RefreshTokenUseCase(
      AuthSessionRepository authSessionRepository,
      RefreshTokenHasher refreshTokenHasher,
      TokenProvider tokenProvider,
      UserReader userReader,
      Clock clock) {
    this.authSessionRepository = authSessionRepository;
    this.refreshTokenHasher = refreshTokenHasher;
    this.tokenProvider = tokenProvider;
    this.userReader = userReader;
    this.clock = clock;
  }

  public AuthResult execute(RefreshTokenCommand command) {
    if (command.refreshToken() == null || command.refreshToken().isBlank()) {
      throw new InvalidRefreshTokenException();
    }

    String refreshTokenHash = refreshTokenHasher.hash(command.refreshToken());
    Instant now = Instant.now(clock);

    // 1. Try to find by active current refresh token hash
    Optional<AuthSession> activeSessionOpt =
        authSessionRepository.findActiveByRefreshTokenHash(refreshTokenHash, now);

    if (activeSessionOpt.isPresent()) {
      AuthSession session = activeSessionOpt.get();
      CreatedUser user = userReader.getUserById(session.userId());

      TokenPair tokenPair = tokenProvider.issueTokens(session.userId(), session.id());
      session.rotateTo(refreshTokenHasher.hash(tokenPair.refreshToken()), now);
      authSessionRepository.save(session);

      return buildTokenRotationResult(user, tokenPair);
    }

    // 2. If not found, check if it matches a previous token (rotated recently)
    Optional<AuthSession> rotatedSessionOpt =
        authSessionRepository.findByPreviousRefreshTokenHash(refreshTokenHash);

    if (rotatedSessionOpt.isPresent()) {
      AuthSession session = rotatedSessionOpt.get();

      // Grace Period: 30 seconds
      if (session.isActive(now)
          && session.rotatedAt() != null
          && now.isBefore(session.rotatedAt().plusSeconds(30))) {

        CreatedUser user = userReader.getUserById(session.userId());
        TokenPair tokenPair = tokenProvider.issueTokens(session.userId(), session.id());

        // Return new access token, but do not return a new refresh token (do not rotate again)
        return buildTokenRotationResult(user, new TokenPair(tokenPair.accessToken(), null));
      } else {
        // Reuse Attack: Revoke session immediately
        session.revoke(now);
        authSessionRepository.save(session);
        throw new InvalidRefreshTokenException();
      }
    }

    throw new InvalidRefreshTokenException();
  }

  private AuthResult buildTokenRotationResult(CreatedUser user, TokenPair tokenPair) {
    AuthenticatedUserDto userDto =
        new AuthenticatedUserDto(
            user.id(),
            user.email(),
            user.username(),
            user.displayName(),
            user.avatarUrl(),
            user.role());

    return new AuthResult(userDto, tokenPair.accessToken(), tokenPair.refreshToken());
  }
}
