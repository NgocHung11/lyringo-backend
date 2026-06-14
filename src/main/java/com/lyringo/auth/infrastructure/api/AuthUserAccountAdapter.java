package com.lyringo.auth.infrastructure.api;

import com.lyringo.auth.application.port.CreateUserAccountRequest;
import com.lyringo.auth.application.port.CreatedUser;
import com.lyringo.auth.application.port.UserCreator;
import com.lyringo.auth.application.port.UserReader;
import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.application.command.CreateUserCommand;
import com.lyringo.users.application.dto.UserDto;
import com.lyringo.users.application.port.UserPublicApi;
import org.springframework.stereotype.Component;

@Component
public class AuthUserAccountAdapter implements UserCreator, UserReader {

  private final UserPublicApi userPublicApi;

  public AuthUserAccountAdapter(UserPublicApi userPublicApi) {
    this.userPublicApi = userPublicApi;
  }

  @Override
  public CreatedUser createUser(CreateUserAccountRequest request) {
    UserDto user =
        userPublicApi.createUser(
            new CreateUserCommand(request.email(), request.username(), request.displayName()));

    return toCreatedUser(user);
  }

  @Override
  public CreatedUser getUserById(UserId userId) {
    UserDto user = userPublicApi.getUserById(userId);
    return toCreatedUser(user);
  }

  private CreatedUser toCreatedUser(UserDto user) {
    return new CreatedUser(
        user.id(),
        user.email(),
        user.username(),
        user.displayName(),
        user.avatarUrl(),
        user.role());
  }
}
