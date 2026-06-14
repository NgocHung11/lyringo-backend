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
import java.time.Instant;

public class RefreshTokenUseCase {

  private final AuthSessionRepository authSessionRepository;
  private final RefreshTokenHasher refreshTokenHasher;
  private final TokenProvider tokenProvider;
  private final UserReader userReader;

  public RefreshTokenUseCase(
      AuthSessionRepository authSessionRepository,
      RefreshTokenHasher refreshTokenHasher,
      TokenProvider tokenProvider,
      UserReader userReader) {
    this.authSessionRepository = authSessionRepository;
    this.refreshTokenHasher = refreshTokenHasher;
    this.tokenProvider = tokenProvider;
    this.userReader = userReader;
  }

  public AuthResult execute(RefreshTokenCommand command) {
    if (command.refreshToken() == null || command.refreshToken().isBlank()) {
      throw new InvalidRefreshTokenException();
    }

    String refreshTokenHash = refreshTokenHasher.hash(command.refreshToken());

    AuthSession session =
        authSessionRepository
            .findByRefreshTokenHash(refreshTokenHash)
            .orElseThrow(InvalidRefreshTokenException::new);

    if (!session.isActive(Instant.now())) {
      throw new InvalidRefreshTokenException();
    }

    CreatedUser user = userReader.getUserById(session.userId());

    TokenPair tokenPair = tokenProvider.issueTokens(session.userId(), session.id());
    session.setRefreshTokenHash(refreshTokenHasher.hash(tokenPair.refreshToken()));
    authSessionRepository.save(session);

    return new AuthResult(
        toAuthenticatedUser(user), tokenPair.accessToken(), tokenPair.refreshToken());
  }

  private AuthenticatedUserDto toAuthenticatedUser(CreatedUser user) {
    return new AuthenticatedUserDto(
        user.id(),
        user.email(),
        user.username(),
        user.displayName(),
        user.avatarUrl(),
        user.role());
  }
}
