package com.lyringo.users.application.usecase;

import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.users.application.command.CreateUserCommand;
import com.lyringo.users.application.dto.UserDto;
import com.lyringo.users.application.port.UserRepository;
import com.lyringo.users.domain.model.User;
import com.lyringo.users.domain.valueobject.Username;

public class CreateUserUseCase {
  private final UserRepository userRepository;

  public CreateUserUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto execute(CreateUserCommand command) {
    Email email = new Email(command.email());
    Username username = new Username(command.username());

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email is already used");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username is already used");
    }

    User user = User.create(email, username, command.displayName());
    User saved = userRepository.save(user);
    return UserDto.fromDomain(saved);
  }
}
