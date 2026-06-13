package com.lyringo.auth.application.port;

import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import java.util.Optional;

public interface AuthSessionRepository {

  AuthSession save(AuthSession session);

  Optional<AuthSession> findById(AuthSessionId id);

  Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash);
}
