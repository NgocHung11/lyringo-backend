package com.lyringo.auth.infrastructure.persistence.jpa;

import com.lyringo.auth.domain.model.AuthIdentity;
import com.lyringo.auth.domain.valueobject.AuthIdentityId;
import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;

final class AuthIdentityJpaMapper {

  private AuthIdentityJpaMapper() {}

  static AuthIdentityJpaEntity toJpa(AuthIdentity identity) {
    return new AuthIdentityJpaEntity(
      identity.id().value(),
      identity.userId().value(),
      identity.provider(),
      identity.providerUserId(),
      identity.email().value(),
      identity.passwordHash(),
      identity.createdAt(),
      identity.updatedAt());
  }

  static AuthIdentity toDomain(AuthIdentityJpaEntity entity) {
    return new AuthIdentity(
      new AuthIdentityId(entity.getId()),
      new UserId(entity.getUserId()),
      entity.getProvider(),
      entity.getProviderUserId(),
      new Email(entity.getEmail()),
      entity.getPasswordHash(),
      entity.getCreatedAt(),
      entity.getUpdatedAt());
  }
}
