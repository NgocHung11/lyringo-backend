package com.lyringo.auth.infrastructure.persistence.jpa;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAuthSessionRepository extends JpaRepository<AuthSessionJpaEntity, UUID> {

  Optional<AuthSessionJpaEntity> findByRefreshTokenHash(String refreshTokenHash);

  Optional<AuthSessionJpaEntity> findByRefreshTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
      String refreshTokenHash, Instant now);
}
