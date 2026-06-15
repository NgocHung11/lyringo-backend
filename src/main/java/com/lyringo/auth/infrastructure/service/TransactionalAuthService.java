package com.lyringo.auth.infrastructure.service;

import com.lyringo.auth.application.command.LoginCommand;
import com.lyringo.auth.application.command.LogoutCommand;
import com.lyringo.auth.application.command.RefreshTokenCommand;
import com.lyringo.auth.application.command.RegisterCommand;
import com.lyringo.auth.application.result.AuthResult;
import com.lyringo.auth.application.usecase.LoginUseCase;
import com.lyringo.auth.application.usecase.LogoutUseCase;
import com.lyringo.auth.application.usecase.RefreshTokenUseCase;
import com.lyringo.auth.application.usecase.RegisterUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionalAuthService {

  private final RegisterUseCase registerUseCase;
  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final LogoutUseCase logoutUseCase;

  public TransactionalAuthService(
      RegisterUseCase registerUseCase,
      LoginUseCase loginUseCase,
      RefreshTokenUseCase refreshTokenUseCase,
      LogoutUseCase logoutUseCase) {
    this.registerUseCase = registerUseCase;
    this.loginUseCase = loginUseCase;
    this.refreshTokenUseCase = refreshTokenUseCase;
    this.logoutUseCase = logoutUseCase;
  }

  @Transactional
  public AuthResult register(RegisterCommand command) {
    return registerUseCase.execute(command);
  }

  @Transactional
  public AuthResult login(LoginCommand command) {
    return loginUseCase.execute(command);
  }

  @Transactional
  public AuthResult refresh(RefreshTokenCommand command) {
    return refreshTokenUseCase.execute(command);
  }

  @Transactional
  public void logout(LogoutCommand command) {
    logoutUseCase.execute(command);
  }
}
