package com.lyringo.shared.infrastructure.security;

import com.lyringo.shared.application.security.AccessTokenPayload;
import com.lyringo.shared.application.security.AccessTokenVerifier;
import com.lyringo.shared.application.security.InvalidAccessTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  static final String[] PUBLIC_ENDPOINTS = {
    "/error",
    "/actuator/health/**",
    "/api/v1",
    "/api/v1/auth/csrf",
    "/api/v1/auth/register",
    "/api/v1/auth/login",
    "/api/v1/auth/refresh"
  };

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String INVALID_ACCESS_TOKEN_MESSAGE = "Invalid access token";
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final AccessTokenVerifier accessTokenVerifier;
  private final List<RequestMatcher> publicEndpointMatchers;

  public JwtAuthenticationFilter(AccessTokenVerifier accessTokenVerifier) {
    this.accessTokenVerifier = accessTokenVerifier;
    this.publicEndpointMatchers =
        Stream.of(PUBLIC_ENDPOINTS)
            .<RequestMatcher>map(PathPatternRequestMatcher.withDefaults()::matcher)
            .toList();
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return publicEndpointMatchers.stream().anyMatch(matcher -> matcher.matches(request));
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String accessToken = extractBearerToken(request);

    if (accessToken == null) {
      writeUnauthorized(response);
      return;
    }

    try {
      AccessTokenPayload payload = accessTokenVerifier.verify(accessToken);

      AuthenticatedUserPrincipal principal =
          new AuthenticatedUserPrincipal(payload.userId(), payload.sessionId(), payload.tokenId());

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(principal, null, List.of());

      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } catch (InvalidAccessTokenException exception) {
      SecurityContextHolder.clearContext();
      LOGGER.debug("Invalid access token rejected", exception);
      writeUnauthorized(response);
    }
  }

  private String extractBearerToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }

    String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

    if (token.isBlank()) {
      return null;
    }

    return token;
  }

  private void writeUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response
        .getWriter()
        .write(
            """
            {"code":"UNAUTHORIZED","message":"%s","details":{}}
            """
                .formatted(INVALID_ACCESS_TOKEN_MESSAGE));
  }
}
