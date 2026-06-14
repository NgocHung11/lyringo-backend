package com.lyringo.auth.infrastructure.persistence.jpa;

import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAuthSessionRepositoryAdapter implements AuthSessionRepository {

  private final SpringDataAuthSessionRepository springDataAuthSessionRepository;

  public JpaAuthSessionRepositoryAdapter(
      SpringDataAuthSessionRepository springDataAuthSessionRepository) {
    this.springDataAuthSessionRepository = springDataAuthSessionRepository;
  }

  @Override
  public AuthSession save(AuthSession session) {
    AuthSessionJpaEntity entity = AuthSessionJpaMapper.toJpa(session);
    AuthSessionJpaEntity saved = springDataAuthSessionRepository.save(entity);
    return AuthSessionJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<AuthSession> findById(AuthSessionId id) {
    return springDataAuthSessionRepository.findById(id.value()).map(AuthSessionJpaMapper::toDomain);
  }

  @Override
  public Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash) {
    return springDataAuthSessionRepository
        .findByRefreshTokenHash(refreshTokenHash)
        .map(AuthSessionJpaMapper::toDomain);
  }

  @Override
  public Optional<AuthSession> findActiveByRefreshTokenHash(String refreshTokenHash, Instant now) {
    return springDataAuthSessionRepository
        .findByRefreshTokenHashAndRevokedAtIsNullAndExpiresAtAfter(refreshTokenHash, now)
        .map(AuthSessionJpaMapper::toDomain);
  }
}
