package com.lyringo.users.infrastructure.persistence.jpa;

import com.lyringo.shared.domain.valueobject.Email;
import com.lyringo.shared.domain.valueobject.UserId;
import com.lyringo.users.application.port.UserRepository;
import com.lyringo.users.domain.model.User;
import com.lyringo.users.domain.valueobject.Username;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository springDataUserRepository;

  public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
    this.springDataUserRepository = springDataUserRepository;
  }

  @Override
  public User save(User user) {
    UserJpaEntity entity = UserJpaMapper.toJpa(user);
    UserJpaEntity saved = springDataUserRepository.save(entity);
    return UserJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<User> findById(UserId id) {
    return springDataUserRepository.findById(id.value()).map(UserJpaMapper::toDomain);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return springDataUserRepository.existsByEmail(email.value());
  }

  @Override
  public boolean existsByUsername(Username username) {
    return springDataUserRepository.existsByUsername(username.value());
  }
}
