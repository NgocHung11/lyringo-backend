package com.lyringo.users.infrastructure.config;

import com.lyringo.users.application.port.UserRepository;
import com.lyringo.users.application.usecase.CreateUserUseCase;
import com.lyringo.users.application.usecase.GetUserByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

  @Bean
  CreateUserUseCase createUserUseCase(UserRepository userRepository) {
    return new CreateUserUseCase(userRepository);
  }

  @Bean
  GetUserByIdUseCase getUserByIdUseCase(UserRepository userRepository) {
    return new GetUserByIdUseCase(userRepository);
  }
}
