package com.lyringo.users.application.usecase;

import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.application.dto.UserDto;
import com.lyringo.users.application.port.UserRepository;
import com.lyringo.users.domain.exception.UserNotFoundException;
import com.lyringo.users.domain.model.User;

public class GetUserByIdUseCase {

  private final UserRepository userRepository;

  public GetUserByIdUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDto execute(UserId userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    return UserDto.fromDomain(user);
  }
}
