package com.lyringo.shared.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyringo.shared.application.security.AccessTokenPayload;
import com.lyringo.shared.application.security.AccessTokenVerifier;
import jakarta.servlet.FilterChain;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

  private final AccessTokenVerifier accessTokenVerifier = mock(AccessTokenVerifier.class);
  private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(accessTokenVerifier);

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void skipsPublicEndpointsWithoutToken() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    filter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(accessTokenVerifier, never()).verify(anyString());
  }

  @Test
  void rejectsProtectedEndpointsWithoutToken() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/me");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    filter.doFilter(request, response, filterChain);

    assertThat(response.getStatus()).isEqualTo(401);
    verify(filterChain, never()).doFilter(request, response);
    verify(accessTokenVerifier, never()).verify(anyString());
  }

  @Test
  void authenticatesProtectedEndpointsWithValidBearerToken() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID sessionId = UUID.randomUUID();
    AccessTokenPayload payload =
        new AccessTokenPayload(userId, sessionId, "token-id", Instant.now().plusSeconds(60));
    when(accessTokenVerifier.verify("valid-token")).thenReturn(payload);

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/me");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    filter.doFilter(request, response, filterChain);

    AuthenticatedUserPrincipal principal =
        (AuthenticatedUserPrincipal)
            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    assertThat(principal.userId()).isEqualTo(userId);
    assertThat(principal.sessionId()).isEqualTo(sessionId);
    verify(filterChain).doFilter(request, response);
  }
}
