package com.lyringo.auth.infrastructure.persistence.jpa;

import com.lyringo.auth.application.port.AuthIdentityRepository;
import com.lyringo.auth.domain.model.AuthIdentity;
import com.lyringo.auth.domain.model.AuthProvider;
import com.lyringo.shared.domain.valueobject.Email;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAuthIdentityRepositoryAdapter implements AuthIdentityRepository {

  private final SpringDataAuthIdentityRepository springDataAuthIdentityRepository;

  public JpaAuthIdentityRepositoryAdapter(
      SpringDataAuthIdentityRepository springDataAuthIdentityRepository) {
    this.springDataAuthIdentityRepository = springDataAuthIdentityRepository;
  }

  @Override
  public AuthIdentity save(AuthIdentity identity) {
    AuthIdentityJpaEntity entity = AuthIdentityJpaMapper.toJpa(identity);
    AuthIdentityJpaEntity saved = springDataAuthIdentityRepository.save(entity);
    return AuthIdentityJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<AuthIdentity> findEmailPasswordIdentityByEmail(Email email) {
    return springDataAuthIdentityRepository
        .findByProviderAndEmail(AuthProvider.EMAIL_PASSWORD, email.value())
        .map(AuthIdentityJpaMapper::toDomain);
  }

  @Override
  public boolean existsEmailPasswordIdentityByEmail(Email email) {
    return springDataAuthIdentityRepository.existsByProviderAndEmail(
        AuthProvider.EMAIL_PASSWORD, email.value());
  }
}
