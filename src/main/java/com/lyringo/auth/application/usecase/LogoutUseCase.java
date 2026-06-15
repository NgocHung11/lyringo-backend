package com.lyringo.auth.application.usecase;

import com.lyringo.auth.application.command.LogoutCommand;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.domain.valueobject.AuthSessionId;

public class LogoutUseCase {

  private final AuthSessionRepository authSessionRepository;
  private final RefreshTokenHasher refreshTokenHasher;

  public LogoutUseCase(
      AuthSessionRepository authSessionRepository, RefreshTokenHasher refreshTokenHasher) {
    this.authSessionRepository = authSessionRepository;
    this.refreshTokenHasher = refreshTokenHasher;
  }

  public void execute(LogoutCommand command) {
    if (command.refreshToken() != null && !command.refreshToken().isBlank()) {
      String hash = refreshTokenHasher.hash(command.refreshToken());
      authSessionRepository
          .findByRefreshTokenHash(hash)
          .ifPresent(
              session -> {
                session.revoke();
                authSessionRepository.save(session);
              });
    }

    if (command.sessionId() != null) {
      authSessionRepository
          .findById(new AuthSessionId(command.sessionId()))
          .ifPresent(
              session -> {
                if (session.revokedAt() == null) {
                  session.revoke();
                  authSessionRepository.save(session);
                }
              });
    }
  }
}
