package com.lyringo.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
    HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(
        authorize ->
          authorize
            .requestMatchers(
              "/error",
              "/actuator/health/**",
              "/api/v1",
              "/api/v1/auth/register",
              "/api/v1/auth/login",
              "/api/v1/auth/refresh")
            .permitAll()
            .anyRequest()
            .authenticated())
      .exceptionHandling(
        exception ->
          exception.authenticationEntryPoint(
            (request, response, authException) ->
              writeJsonError(
                response
              )))
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  private static void writeJsonError(
    HttpServletResponse response) throws IOException {
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
