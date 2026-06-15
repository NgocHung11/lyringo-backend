package com.lyringo.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyringo.auth.application.command.RefreshTokenCommand;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.CreatedUser;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.application.port.TokenPair;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.application.port.UserReader;
import com.lyringo.auth.application.result.AuthResult;
import com.lyringo.auth.domain.exception.InvalidRefreshTokenException;
import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class RefreshTokenUseCaseTest {

  private static final Instant FIXED_NOW = Instant.parse("2026-01-01T00:00:00Z");

  private final AuthSessionRepository authSessionRepository = mock(AuthSessionRepository.class);
  private final RefreshTokenHasher refreshTokenHasher = mock(RefreshTokenHasher.class);
  private final TokenProvider tokenProvider = mock(TokenProvider.class);
  private final UserReader userReader = mock(UserReader.class);

  private final RefreshTokenUseCase refreshTokenUseCase =
      new RefreshTokenUseCase(authSessionRepository, refreshTokenHasher, tokenProvider, userReader);

  @Test
  void shouldResolveUserBeforeIssuingRotatedTokens() {
    UserId userId = UserId.newId();
    AuthSession session = activeSession(userId);
    CreatedUser user =
        new CreatedUser(
            userId.value().toString(),
            "lexi@example.com",
            "lexi",
            "Lexi",
            "https://cdn.example.com/lexi.png",
            "USER");

    when(refreshTokenHasher.hash("incoming-refresh-token")).thenReturn("existing-refresh-hash");
    when(authSessionRepository.findActiveByRefreshTokenHash(eq("existing-refresh-hash"), any()))
        .thenReturn(Optional.of(session));
    when(userReader.getUserById(userId)).thenReturn(user);
    when(tokenProvider.issueTokens(userId, session.id()))
        .thenReturn(new TokenPair("access-token", "rotated-refresh-token"));
    when(refreshTokenHasher.hash("rotated-refresh-token")).thenReturn("rotated-refresh-hash");

    AuthResult result =
        refreshTokenUseCase.execute(new RefreshTokenCommand("incoming-refresh-token"));

    assertThat(result.accessToken()).isEqualTo("access-token");
    assertThat(result.refreshToken()).isEqualTo("rotated-refresh-token");
    assertThat(result.user().id()).isEqualTo(userId.value().toString());
    assertThat(session.refreshTokenHash()).isEqualTo("rotated-refresh-hash");

    InOrder order = inOrder(userReader, tokenProvider, authSessionRepository);
    order
        .verify(authSessionRepository)
        .findActiveByRefreshTokenHash(eq("existing-refresh-hash"), any());
    order.verify(userReader).getUserById(userId);
    order.verify(tokenProvider).issueTokens(userId, session.id());
    order.verify(authSessionRepository).save(session);
  }

  @Test
  void shouldNotIssueOrPersistRotatedTokensWhenSessionUserCannotBeResolved() {
    UserId userId = UserId.newId();
    AuthSession session = activeSession(userId);
    RuntimeException unresolvedUser = new RuntimeException("User not found");

    when(refreshTokenHasher.hash("incoming-refresh-token")).thenReturn("existing-refresh-hash");
    when(authSessionRepository.findActiveByRefreshTokenHash(eq("existing-refresh-hash"), any()))
        .thenReturn(Optional.of(session));
    when(userReader.getUserById(userId)).thenThrow(unresolvedUser);

    assertThatThrownBy(
            () -> refreshTokenUseCase.execute(new RefreshTokenCommand("incoming-refresh-token")))
        .isSameAs(unresolvedUser);

    assertThat(session.refreshTokenHash()).isEqualTo("existing-refresh-hash");
    verify(tokenProvider, never()).issueTokens(userId, session.id());
    verify(authSessionRepository, never()).save(session);
  }

  @Test
  void shouldReturnNewAccessTokenAndNullRefreshTokenWithinGracePeriod() {
    UserId userId = UserId.newId();
    Instant realNow = Instant.now();
    AuthSession session =
        new AuthSession(
            AuthSessionId.newId(),
            userId,
            "new-refresh-hash",
            "previous-refresh-hash",
            "JUnit",
            "127.0.0.1",
            null,
            realNow.plusSeconds(300),
            realNow.minusSeconds(10), // rotated 10s ago (within 30s grace)
            realNow.minusSeconds(60),
            realNow.minusSeconds(60));

    CreatedUser user =
        new CreatedUser(
            userId.value().toString(),
            "lexi@example.com",
            "lexi",
            "Lexi",
            "https://cdn.example.com/lexi.png",
            "USER");

    when(refreshTokenHasher.hash("incoming-refresh-token")).thenReturn("previous-refresh-hash");
    when(authSessionRepository.findByPreviousRefreshTokenHash("previous-refresh-hash"))
        .thenReturn(Optional.of(session));
    when(userReader.getUserById(userId)).thenReturn(user);
    when(tokenProvider.issueTokens(userId, session.id()))
        .thenReturn(new TokenPair("new-access-token", "not-returned-refresh-token"));

    AuthResult result =
        refreshTokenUseCase.execute(new RefreshTokenCommand("incoming-refresh-token"));

    assertThat(result.accessToken()).isEqualTo("new-access-token");
    assertThat(result.refreshToken()).isNull();
    assertThat(result.user().id()).isEqualTo(userId.value().toString());
    // Ensure session properties are not modified
    assertThat(session.refreshTokenHash()).isEqualTo("new-refresh-hash");
    verify(authSessionRepository, never()).save(session);
  }

  @Test
  void shouldRevokeSessionAndThrowExceptionOnReuseOutsideGracePeriod() {
    UserId userId = UserId.newId();
    Instant realNow = Instant.now();
    AuthSession session =
        new AuthSession(
            AuthSessionId.newId(),
            userId,
            "new-refresh-hash",
            "previous-refresh-hash",
            "JUnit",
            "127.0.0.1",
            null,
            realNow.plusSeconds(300),
            realNow.minusSeconds(35), // rotated 35s ago (outside 30s grace)
            realNow.minusSeconds(60),
            realNow.minusSeconds(60));

    when(refreshTokenHasher.hash("incoming-refresh-token")).thenReturn("previous-refresh-hash");
    when(authSessionRepository.findByPreviousRefreshTokenHash("previous-refresh-hash"))
        .thenReturn(Optional.of(session));

    assertThatThrownBy(
            () -> refreshTokenUseCase.execute(new RefreshTokenCommand("incoming-refresh-token")))
        .isInstanceOf(InvalidRefreshTokenException.class);

    assertThat(session.revokedAt()).isNotNull();
    verify(authSessionRepository).save(session);
    verify(tokenProvider, never()).issueTokens(any(), any());
  }

  private AuthSession activeSession(UserId userId) {
    return new AuthSession(
        AuthSessionId.newId(),
        userId,
        "existing-refresh-hash",
        null,
        "JUnit",
        "127.0.0.1",
        null,
        FIXED_NOW.plusSeconds(300),
        null,
        FIXED_NOW.minusSeconds(60),
        FIXED_NOW.minusSeconds(60));
  }
}
