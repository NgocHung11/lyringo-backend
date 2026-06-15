package com.lyringo.auth.application.usecase;

import com.lyringo.auth.application.command.LogoutCommand;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import java.time.Clock;
import java.time.Instant;

public class LogoutUseCase {

  private final AuthSessionRepository authSessionRepository;
  private final RefreshTokenHasher refreshTokenHasher;
  private final Clock clock;

  public LogoutUseCase(
      AuthSessionRepository authSessionRepository,
      RefreshTokenHasher refreshTokenHasher,
      Clock clock) {
    this.authSessionRepository = authSessionRepository;
    this.refreshTokenHasher = refreshTokenHasher;
    this.clock = clock;
  }

  public void execute(LogoutCommand command) {
    Instant now = Instant.now(clock);
    if (command.refreshToken() != null && !command.refreshToken().isBlank()) {
      String hash = refreshTokenHasher.hash(command.refreshToken());
      authSessionRepository
          .findByRefreshTokenHash(hash)
          .ifPresent(
              session -> {
                session.revoke(now);
                authSessionRepository.save(session);
              });
    }

    if (command.sessionId() != null) {
      authSessionRepository
          .findById(new AuthSessionId(command.sessionId()))
          .ifPresent(
              session -> {
                if (session.revokedAt() == null) {
                  session.revoke(now);
                  authSessionRepository.save(session);
                }
              });
    }
  }
}
