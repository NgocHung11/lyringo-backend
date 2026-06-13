package com.lyringo.auth.application.port;

import com.lyringo.shared.domain.valueobject.UserId;

public interface UserReader {
  CreatedUser getUserById(UserId userId);
}
