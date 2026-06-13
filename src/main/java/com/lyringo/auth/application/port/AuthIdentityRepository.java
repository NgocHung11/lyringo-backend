package com.lyringo.auth.application.port;

import com.lyringo.auth.domain.model.AuthIdentity;
import com.lyringo.shared.domain.valueobject.Email;
import java.util.Optional;

public interface AuthIdentityRepository {
  AuthIdentity save(AuthIdentity identity);

  Optional<AuthIdentity> findEmailPasswordIdentityByEmail(Email email);

  boolean existsEmailPasswordIdentityByEmail(Email email);
}
