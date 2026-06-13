package com.lyringo.auth.application.port;

public interface UserCreator {
  CreatedUser createUser(CreateUserAccountRequest request);
}
