package com.lyringo.users.application.port;

import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.application.command.CreateUserCommand;
import com.lyringo.users.application.dto.UserDto;

public interface UserPublicApi {
  UserDto createUser(CreateUserCommand command);

  UserDto getUserById(UserId userId);
}
