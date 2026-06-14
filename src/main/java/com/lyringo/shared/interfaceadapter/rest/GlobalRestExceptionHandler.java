package com.lyringo.shared.interfaceadapter.rest;

import com.lyringo.auth.domain.exception.InvalidCredentialsException;
import com.lyringo.auth.domain.exception.InvalidRefreshTokenException;
import com.lyringo.users.domain.exception.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
      InvalidCredentialsException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiErrorResponse.of("INVALID_CREDENTIALS", exception.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiErrorResponse.of("USER_NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
      IllegalArgumentException exception) {
    HttpStatus status =
        exception.getMessage() != null
                && exception.getMessage().toLowerCase().contains("already used")
            ? HttpStatus.CONFLICT
            : HttpStatus.BAD_REQUEST;

    return ResponseEntity.status(status)
        .body(ApiErrorResponse.of("BAD_REQUEST", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
      MethodArgumentNotValidException exception) {
    Map<String, String> details = new HashMap<>();

    for (FieldError error : exception.getBindingResult().getFieldErrors()) {
      details.put(error.getField(), error.getDefaultMessage());
    }

    return ResponseEntity.badRequest()
        .body(ApiErrorResponse.of("VALIDATION_ERROR", "Request validation failed", details));
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidRefreshToken(
      InvalidRefreshTokenException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiErrorResponse.of("INVALID_REFRESH_TOKEN", exception.getMessage()));
  }
}
