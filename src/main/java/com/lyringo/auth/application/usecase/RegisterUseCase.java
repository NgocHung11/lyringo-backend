package com.lyringo.auth.application.usecase;

import com.lyringo.auth.application.command.RegisterCommand;
import com.lyringo.auth.application.dto.AuthenticatedUserDto;
import com.lyringo.auth.application.port.AuthIdentityRepository;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.CreateUserAccountRequest;
import com.lyringo.auth.application.port.CreatedUser;
import com.lyringo.auth.application.port.PasswordHasher;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.application.port.TokenPair;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.application.port.UserCreator;
import com.lyringo.auth.application.result.AuthResult;
import com.lyringo.auth.domain.model.AuthIdentity;
import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class RegisterUseCase {
  private final AuthIdentityRepository authIdentityRepository;
  private final AuthSessionRepository authSessionRepository;
  private final PasswordHasher passwordHasher;
  private final RefreshTokenHasher refreshTokenHasher;
  private final TokenProvider tokenProvider;
  private final UserCreator userCreator;

  public RegisterUseCase(
    AuthIdentityRepository authIdentityRepository,
    AuthSessionRepository authSessionRepository,
    PasswordHasher passwordHasher,
    RefreshTokenHasher refreshTokenHasher,
    TokenProvider tokenProvider,
    UserCreator userCreator) {
    this.authIdentityRepository = authIdentityRepository;
    this.authSessionRepository = authSessionRepository;
    this.passwordHasher = passwordHasher;
    this.refreshTokenHasher = refreshTokenHasher;
    this.tokenProvider = tokenProvider;
    this.userCreator = userCreator;
  }

  public AuthResult execute(RegisterCommand command) {
    Email email = new Email(command.email());
    if (authIdentityRepository.existsEmailPasswordIdentityByEmail(email)) {
      throw new IllegalArgumentException("Email is already used");
    }

    CreatedUser user =
      userCreator.createUser(
        new CreateUserAccountRequest(
          command.email(), command.username(), command.displayName()));
    UserId userId = new UserId(UUID.fromString(user.id()));

    AuthIdentity identity =
      AuthIdentity.emailPassword(userId, email, passwordHasher.hash(command.password()));
    authIdentityRepository.save(identity);

    AuthSession session =
      AuthSession.create(
        userId,
        command.userAgent(),
        command.ipAddress(),
        Instant.now().plus(Duration.ofDays(30)));
    authSessionRepository.save(session);

    TokenPair tokenPair = tokenProvider.issueTokens(userId, session.id());
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
