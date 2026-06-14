package com.lyringo.users.interfaceadapter.rest;

import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.shared.infrastructure.security.AuthenticatedUserPrincipal;
import com.lyringo.users.application.dto.UserDto;
import com.lyringo.users.application.usecase.GetUserByIdUseCase;
import com.lyringo.users.interfaceadapter.rest.response.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final GetUserByIdUseCase getUserByIdUseCase;

  public UserController(GetUserByIdUseCase getUserByIdUseCase) {
    this.getUserByIdUseCase = getUserByIdUseCase;
  }

  @GetMapping("/api/v1/users/me")
  public UserProfileResponse me(Authentication authentication) {
    AuthenticatedUserPrincipal principal =
        (AuthenticatedUserPrincipal) authentication.getPrincipal();

    UserDto user = getUserByIdUseCase.execute(new UserId(principal.userId()));

    return UserProfileResponse.from(user);
  }
}
