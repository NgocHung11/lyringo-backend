package com.lyringo.auth.infrastructure.config;

import com.lyringo.auth.application.port.AuthIdentityRepository;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.application.port.PasswordHasher;
import com.lyringo.auth.application.port.RefreshTokenHasher;
import com.lyringo.auth.application.port.TokenProvider;
import com.lyringo.auth.application.port.UserCreator;
import com.lyringo.auth.application.port.UserReader;
import com.lyringo.auth.application.usecase.LoginUseCase;
import com.lyringo.auth.application.usecase.RefreshTokenUseCase;
import com.lyringo.auth.application.usecase.RegisterUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

  @Bean
  RegisterUseCase registerUseCase(
    AuthIdentityRepository authIdentityRepository,
    AuthSessionRepository authSessionRepository,
    PasswordHasher passwordHasher,
    RefreshTokenHasher refreshTokenHasher,
    TokenProvider tokenProvider,
    UserCreator userCreator) {
    return new RegisterUseCase(
      authIdentityRepository,
      authSessionRepository,
      passwordHasher,
      refreshTokenHasher,
      tokenProvider,
      userCreator);
  }

  @Bean
  LoginUseCase loginUseCase(
    AuthIdentityRepository authIdentityRepository,
    AuthSessionRepository authSessionRepository,
    PasswordHasher passwordHasher,
    RefreshTokenHasher refreshTokenHasher,
    TokenProvider tokenProvider,
    UserReader userReader) {
    return new LoginUseCase(
      authIdentityRepository,
      authSessionRepository,
      passwordHasher,
      refreshTokenHasher,
      tokenProvider,
      userReader);
  }

  @Bean
  RefreshTokenUseCase refreshTokenUseCase(
    AuthSessionRepository authSessionRepository,
    RefreshTokenHasher refreshTokenHasher,
    TokenProvider tokenProvider,
    UserReader userReader) {
    return new RefreshTokenUseCase(
      authSessionRepository, refreshTokenHasher, tokenProvider, userReader);
  }
}
