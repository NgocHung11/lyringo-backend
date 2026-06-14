package com.lyringo.auth.infrastructure.persistence.jpa;

import com.lyringo.auth.domain.model.AuthProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataAuthIdentityRepository extends JpaRepository<AuthIdentityJpaEntity, UUID> {

  Optional<AuthIdentityJpaEntity> findByProviderAndEmail(AuthProvider provider, String email);

  boolean existsByProviderAndEmail(AuthProvider provider, String email);
}
