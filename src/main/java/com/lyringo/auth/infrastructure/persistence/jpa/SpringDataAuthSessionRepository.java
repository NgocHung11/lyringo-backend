package com.lyringo.auth.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAuthSessionRepository extends JpaRepository<AuthSessionJpaEntity, UUID> {

  Optional<AuthSessionJpaEntity> findByRefreshTokenHash(String refreshTokenHash);
}
