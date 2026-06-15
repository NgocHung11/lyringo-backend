package com.lyringo.auth.infrastructure.config;

import com.lyringo.auth.application.port.AuthIdentityRepository;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.PasswordHasher;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.application.port.UserCreator;
import com.lyringo.auth.application.port.UserReader;
import com.lyringo.auth.application.usecase.LoginUseCase;
import com.lyringo.auth.application.usecase.LogoutUseCase;
import com.lyringo.auth.application.usecase.RefreshTokenUseCase;
import com.lyringo.auth.application.usecase.RegisterUseCase;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  RegisterUseCase registerUseCase(
      AuthIdentityRepository authIdentityRepository,
      AuthSessionRepository authSessionRepository,
      PasswordHasher passwordHasher,
      RefreshTokenHasher refreshTokenHasher,
      TokenProvider tokenProvider,
      UserCreator userCreator,
      Clock clock) {
    return new RegisterUseCase(
        authIdentityRepository,
        authSessionRepository,
        passwordHasher,
        refreshTokenHasher,
        tokenProvider,
        userCreator,
        clock);
  }

  @Bean
  LoginUseCase loginUseCase(
      AuthIdentityRepository authIdentityRepository,
      AuthSessionRepository authSessionRepository,
      PasswordHasher passwordHasher,
      RefreshTokenHasher refreshTokenHasher,
      TokenProvider tokenProvider,
      UserReader userReader,
      Clock clock) {
    return new LoginUseCase(
        authIdentityRepository,
        authSessionRepository,
        passwordHasher,
        refreshTokenHasher,
        tokenProvider,
        userReader,
        clock);
  }

  @Bean
  RefreshTokenUseCase refreshTokenUseCase(
      AuthSessionRepository authSessionRepository,
      RefreshTokenHasher refreshTokenHasher,
      TokenProvider tokenProvider,
      UserReader userReader,
      Clock clock) {
    return new RefreshTokenUseCase(
        authSessionRepository, refreshTokenHasher, tokenProvider, userReader, clock);
  }

  @Bean
  LogoutUseCase logoutUseCase(
      AuthSessionRepository authSessionRepository,
      RefreshTokenHasher refreshTokenHasher,
      Clock clock) {
    return new LogoutUseCase(authSessionRepository, refreshTokenHasher, clock);
  }
}
