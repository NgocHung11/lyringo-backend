package com.lyringo.auth.application.usecase;

import com.lyringo.auth.application.command.LoginCommand;
import com.lyringo.auth.application.dto.AuthenticatedUserDto;
import com.lyringo.auth.application.port.AuthIdentityRepository;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.CreatedUser;
import com.lyringo.auth.application.port.PasswordHasher;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.application.port.TokenPair;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.application.port.UserReader;
import com.lyringo.auth.application.result.AuthResult;
import com.lyringo.auth.domain.exception.InvalidCredentialsException;
import com.lyringo.auth.domain.model.AuthIdentity;
import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.shared.domain.valueobject.Email;
import java.time.Duration;
import java.time.Instant;

public class LoginUseCase {
  private final AuthIdentityRepository authIdentityRepository;
  private final AuthSessionRepository authSessionRepository;
  private final PasswordHasher passwordHasher;
  private final RefreshTokenHasher refreshTokenHasher;
  private final TokenProvider tokenProvider;
  private final UserReader userReader;

  public LoginUseCase(
      AuthIdentityRepository authIdentityRepository,
      AuthSessionRepository authSessionRepository,
      PasswordHasher passwordHasher,
      RefreshTokenHasher refreshTokenHasher,
      TokenProvider tokenProvider,
      UserReader userReader) {
    this.authIdentityRepository = authIdentityRepository;
    this.authSessionRepository = authSessionRepository;
    this.passwordHasher = passwordHasher;
    this.refreshTokenHasher = refreshTokenHasher;
    this.tokenProvider = tokenProvider;
    this.userReader = userReader;
  }

  public AuthResult execute(LoginCommand command) {
    Email email = new Email(command.email());
    AuthIdentity identity =
        authIdentityRepository
            .findEmailPasswordIdentityByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

    if (!passwordHasher.matches(command.password(), identity.passwordHash())) {
      throw new InvalidCredentialsException();
    }

    AuthSession session =
        AuthSession.create(
            identity.userId(),
            command.userAgent(),
            command.ipAddress(),
            Instant.now().plus(Duration.ofDays(30)));
    authSessionRepository.save(session);

    TokenPair tokenPair = tokenProvider.issueTokens(identity.userId(), session.id());
    session.setRefreshTokenHash(refreshTokenHasher.hash(tokenPair.refreshToken()));
    authSessionRepository.save(session);

    CreatedUser user = userReader.getUserById(identity.userId());
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
