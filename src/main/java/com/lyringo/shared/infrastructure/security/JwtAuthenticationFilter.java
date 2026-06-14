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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String INVALID_ACCESS_TOKEN_MESSAGE = "Invalid access token";
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final AccessTokenVerifier accessTokenVerifier;

  public JwtAuthenticationFilter(AccessTokenVerifier accessTokenVerifier) {
    this.accessTokenVerifier = accessTokenVerifier;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String accessToken = extractBearerToken(request);

    if (accessToken == null) {
      filterChain.doFilter(request, response);
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
