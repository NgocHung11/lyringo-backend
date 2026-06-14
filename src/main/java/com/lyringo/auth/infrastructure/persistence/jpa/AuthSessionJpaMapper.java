package com.lyringo.auth.infrastructure.persistence.jpa;

import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.domain.valueobject.UserId;

final class AuthSessionJpaMapper {

  private AuthSessionJpaMapper() {}

  static AuthSessionJpaEntity toJpa(AuthSession session) {
    return new AuthSessionJpaEntity(
        session.id().value(),
        session.userId().value(),
        session.refreshTokenHash(),
        session.userAgent(),
        session.ipAddress(),
        session.revokedAt(),
        session.expiresAt(),
        session.createdAt(),
        session.updatedAt());
  }

  static AuthSession toDomain(AuthSessionJpaEntity entity) {
    return new AuthSession(
        new AuthSessionId(entity.getId()),
        new UserId(entity.getUserId()),
        entity.getRefreshTokenHash(),
        entity.getUserAgent(),
        entity.getIpAddress(),
        entity.getRevokedAt(),
        entity.getExpiresAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
