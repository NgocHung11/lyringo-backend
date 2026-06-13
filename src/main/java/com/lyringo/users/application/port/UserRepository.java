package com.lyringo.users.application.port;

import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.domain.model.User;
import com.lyringo.users.domain.valueobject.Username;
import java.util.Optional;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(UserId id);

  boolean existsByEmail(Email email);

  boolean existsByUsername(Username username);
}
