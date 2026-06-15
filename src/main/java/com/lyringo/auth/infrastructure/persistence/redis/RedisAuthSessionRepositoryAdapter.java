package com.lyringo.auth.infrastructure.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyringo.auth.application.port.AuthSessionRepository;
import com.lyringo.auth.domain.model.AuthSession;
import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.domain.valueobject.UserId;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Repository;

@Repository
public class RedisAuthSessionRepositoryAdapter implements AuthSessionRepository {

  private static final String SESSION_KEY_PREFIX = "session:";
  private static final String TOKEN_HASH_KEY_PREFIX = "session:token-hash:";
  private static final String PREV_TOKEN_HASH_KEY_PREFIX = "session:prev-token-hash:";

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public RedisAuthSessionRepositoryAdapter(
      StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public AuthSession save(AuthSession session) {
    String sessionKey = SESSION_KEY_PREFIX + session.id().value().toString();
    RedisAuthSessionDto dto = RedisAuthSessionDto.fromDomain(session);

    try {
      String json = objectMapper.writeValueAsString(dto);
      Duration ttl = Duration.between(Instant.now(), session.expiresAt());
      if (ttl.isNegative() || ttl.isZero()) {
        ttl = Duration.ofSeconds(1);
      }

      // 1. Save session JSON
      redisTemplate.opsForValue().set(sessionKey, json, ttl);

      // 2. Manage current token hash index
      if (session.refreshTokenHash() != null) {
        String tokenKey = TOKEN_HASH_KEY_PREFIX + session.refreshTokenHash();
        redisTemplate.opsForValue().set(tokenKey, session.id().value().toString(), ttl);
      }

      // 3. Manage previous token hash index and cleanup rotated hash
      if (session.previousRefreshTokenHash() != null) {
        String prevTokenKey = PREV_TOKEN_HASH_KEY_PREFIX + session.previousRefreshTokenHash();
        redisTemplate.opsForValue().set(prevTokenKey, session.id().value().toString(), ttl);

        // Delete from current token hash index since it is now rotated to previous hash
        redisTemplate.delete(TOKEN_HASH_KEY_PREFIX + session.previousRefreshTokenHash());
      }
    } catch (JsonProcessingException e) {
      throw new SerializationException("Failed to serialize AuthSession to JSON", e);
    }

    return session;
  }

  @Override
  public Optional<AuthSession> findById(AuthSessionId id) {
    String sessionKey = SESSION_KEY_PREFIX + id.value().toString();
    String json = redisTemplate.opsForValue().get(sessionKey);
    if (json == null) {
      return Optional.empty();
    }

    try {
      RedisAuthSessionDto dto = objectMapper.readValue(json, RedisAuthSessionDto.class);
      return Optional.of(dto.toDomain());
    } catch (JsonProcessingException e) {
      throw new SerializationException("Failed to deserialize AuthSession from JSON", e);
    }
  }

  @Override
  public Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash) {
    if (refreshTokenHash == null || refreshTokenHash.isBlank()) {
      return Optional.empty();
    }
    String tokenKey = TOKEN_HASH_KEY_PREFIX + refreshTokenHash;
    String sessionIdStr = redisTemplate.opsForValue().get(tokenKey);
    if (sessionIdStr == null) {
      return Optional.empty();
    }
    return findById(new AuthSessionId(UUID.fromString(sessionIdStr)));
  }

  @Override
  public Optional<AuthSession> findByPreviousRefreshTokenHash(String previousRefreshTokenHash) {
    if (previousRefreshTokenHash == null || previousRefreshTokenHash.isBlank()) {
      return Optional.empty();
    }
    String prevTokenKey = PREV_TOKEN_HASH_KEY_PREFIX + previousRefreshTokenHash;
    String sessionIdStr = redisTemplate.opsForValue().get(prevTokenKey);
    if (sessionIdStr == null) {
      return Optional.empty();
    }
    return findById(new AuthSessionId(UUID.fromString(sessionIdStr)));
  }

  @Override
  public Optional<AuthSession> findActiveByRefreshTokenHash(String refreshTokenHash, Instant now) {
    return findByRefreshTokenHash(refreshTokenHash).filter(session -> session.isActive(now));
  }

  private record RedisAuthSessionDto(
      UUID id,
      UUID userId,
      String refreshTokenHash,
      String previousRefreshTokenHash,
      String userAgent,
      String ipAddress,
      Instant revokedAt,
      Instant expiresAt,
      Instant rotatedAt,
      Instant createdAt,
      Instant updatedAt) {

    public static RedisAuthSessionDto fromDomain(AuthSession domain) {
      return new RedisAuthSessionDto(
          domain.id().value(),
          domain.userId().value(),
          domain.refreshTokenHash(),
          domain.previousRefreshTokenHash(),
          domain.userAgent(),
          domain.ipAddress(),
          domain.revokedAt(),
          domain.expiresAt(),
          domain.rotatedAt(),
          domain.createdAt(),
          domain.updatedAt());
    }

    public AuthSession toDomain() {
      return new AuthSession(
          new AuthSessionId(id),
          new UserId(userId),
          refreshTokenHash,
          previousRefreshTokenHash,
          userAgent,
          ipAddress,
          revokedAt,
          expiresAt,
          rotatedAt,
          createdAt,
          updatedAt);
    }
  }
}
