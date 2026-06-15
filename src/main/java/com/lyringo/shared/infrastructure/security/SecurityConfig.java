package com.lyringo.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    return http.csrf(
            csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        "/error",
                        "/actuator/health/**",
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/users/**",
                        "/api/v1/music/**"))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(JwtAuthenticationFilter.PUBLIC_ENDPOINTS)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception.authenticationEntryPoint(
                    (request, response, authException) -> writeJsonError(response)))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private static void writeJsonError(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response
        .getWriter()
        .write(
            """
            {"code":"%s","message":"%s","details":{}}
            """
                .formatted("UNAUTHORIZED", "Authentication is required"));
  }
}
