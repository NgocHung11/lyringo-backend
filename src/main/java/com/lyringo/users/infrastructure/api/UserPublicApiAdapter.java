package com.lyringo.users.infrastructure.api;

import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.application.command.CreateUserCommand;
import com.lyringo.users.application.dto.UserDto;
import com.lyringo.users.application.port.UserPublicApi;
import com.lyringo.users.application.usecase.CreateUserUseCase;
import com.lyringo.users.application.usecase.GetUserByIdUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserPublicApiAdapter implements UserPublicApi {

  private final CreateUserUseCase createUserUseCase;
  private final GetUserByIdUseCase getUserByIdUseCase;

  public UserPublicApiAdapter(
    CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  @Override
  @Transactional
  public UserDto createUser(CreateUserCommand command) {
    return createUserUseCase.execute(command);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserById(UserId userId) {
    return getUserByIdUseCase.execute(userId);
  }
}
