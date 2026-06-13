package com.lyringo.users.infrastructure.persistence.jpa;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
