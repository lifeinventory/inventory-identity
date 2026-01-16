package com.lifeinventory.identity.api.exception;

import com.lifeinventory.identity.api.dto.ErrorResponse;
import com.lifeinventory.identity.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(
            UserNotFoundException ex,
            ServerWebExchange exchange
    ) {
        log.debug("User not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), exchange);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            ServerWebExchange exchange
    ) {
        log.debug("User already exists: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), exchange);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidCredentials(
            InvalidCredentialsException ex,
            ServerWebExchange exchange
    ) {
        log.debug("Invalid credentials: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), exchange);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidToken(
            InvalidTokenException ex,
            ServerWebExchange exchange
    ) {
        log.debug("Invalid token: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), exchange);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTokenExpired(
            TokenExpiredException ex,
            ServerWebExchange exchange
    ) {
        log.debug("Token expired: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), exchange);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotActive(
            UserNotActiveException ex,
            ServerWebExchange exchange
    ) {
        log.debug("User not active: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", "Account is deactivated", exchange);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleEmailNotVerified(
            EmailNotVerifiedException ex,
            ServerWebExchange exchange
    ) {
        log.debug("Email not verified: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", "Email not verified", exchange);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedAccess(
            UnauthorizedAccessException ex,
            ServerWebExchange exchange
    ) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", "Access denied", exchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(
            WebExchangeBindException ex,
            ServerWebExchange exchange
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, exchange);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(
            IllegalArgumentException ex,
            ServerWebExchange exchange
    ) {
        log.debug("Illegal argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), exchange);
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalState(
            IllegalStateException ex,
            ServerWebExchange exchange
    ) {
        log.warn("Illegal state: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(
            Exception ex,
            ServerWebExchange exchange
    ) {
        log.error("Unexpected error: ", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                exchange
        );
    }

    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            ServerWebExchange exchange
    ) {
        String path = exchange.getRequest().getPath().value();
        ErrorResponse errorResponse = ErrorResponse.of(status.value(), error, message, path);
        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
}
