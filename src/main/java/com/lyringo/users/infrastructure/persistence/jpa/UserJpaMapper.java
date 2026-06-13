package com.lyringo.users.infrastructure.persistence.jpa;

import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.domain.model.User;
import com.lyringo.users.domain.valueobject.Username;

final class UserJpaMapper {

  private UserJpaMapper() {}

  static UserJpaEntity toJpa(User user) {
    return new UserJpaEntity(
      user.id().value(),
      user.email().value(),
      user.username().value(),
      user.displayName(),
      user.avatarUrl(),
      user.status(),
      user.role(),
      user.createdAt(),
      user.updatedAt());
  }

  static User toDomain(UserJpaEntity entity) {
    return new User(
      new UserId(entity.getId()),
      new Email(entity.getEmail()),
      new Username(entity.getUsername()),
      entity.getDisplayName(),
      entity.getAvatarUrl(),
      entity.getStatus(),
      entity.getRole(),
      entity.getCreatedAt(),
      entity.getUpdatedAt());
  }
}
