package com.lyringo.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyringo.auth.application.command.LogoutCommand;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LogoutUseCaseTest {

  private final AuthSessionRepository authSessionRepository = mock(AuthSessionRepository.class);
  private final RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);

  private final LogoutUseCase logoutUseCase =
      new LogoutUseCase(authSessionRepository, refreshTokenHasher);

  @Test
  void shouldRevokeSessionByRefreshToken() {
    UserId userId = UserId.newId();
    AuthSession session = activeSession(userId);

    when(refreshTokenHasher.hash("some-refresh-token")).thenReturn("some-refresh-hash");
    when(authSessionRepository.findByRefreshTokenHash("some-refresh-hash"))
        .thenReturn(Optional.of(session));

    logoutUseCase.execute(new LogoutCommand("some-refresh-token", null));

    assertThat(session.revokedAt()).isNotNull();
    verify(authSessionRepository).save(session);
  }

  @Test
  void shouldRevokeSessionBySessionId() {
    UserId userId = UserId.newId();
    AuthSession session = activeSession(userId);
    UUID sessionId = session.id().value();

    when(authSessionRepository.findById(new AuthSessionId(sessionId)))
        .thenReturn(Optional.of(session));

    logoutUseCase.execute(new LogoutCommand(null, sessionId));

    assertThat(session.revokedAt()).isNotNull();
    verify(authSessionRepository).save(session);
  }

  @Test
  void shouldDoNothingIfSessionNotFoundOrAlreadyRevoked() {
    UUID sessionId = UUID.randomUUID();
    when(authSessionRepository.findById(new AuthSessionId(sessionId))).thenReturn(Optional.empty());

    logoutUseCase.execute(new LogoutCommand(null, sessionId));

    verify(authSessionRepository, never()).save(any());
  }

  private AuthSession activeSession(UserId userId) {
    Instant now = Instant.now();
    return new AuthSession(
        AuthSessionId.newId(),
        userId,
        "some-refresh-hash",
        null,
        "JUnit",
        "127.0.0.1",
        null,
        now.plusSeconds(300),
        null,
        now.minusSeconds(60),
        now.minusSeconds(60));
  }
}
