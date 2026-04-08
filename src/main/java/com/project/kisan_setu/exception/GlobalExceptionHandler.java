package com.project.kisan_setu.exception;

import com.project.kisan_setu.dto.ResponseDto.ApiErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //  Custom Exceptions
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUserException(UserException ex,
                                                                   HttpServletRequest request) {

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                ex.getStatus().value(),
                null,
                request.getRequestURI(),
                LocalDateTime.now(),
                ex.getMessage()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }

    // Validation Errors - 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                null,
                request.getRequestURI(),
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //  401 - Unauthorized
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnauthorized(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.UNAUTHORIZED.value(),
                null,
                request.getRequestURI(),
                LocalDateTime.now(),
                ex.getMessage() != null ? ex.getMessage() : "Unauthorized"
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    //  403 - Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDto> handleForbidden(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                null,
                request.getRequestURI(),
                LocalDateTime.now(),
                "Access Denied"
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    //  Fallback - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null,
                request.getRequestURI(),
                LocalDateTime.now(),
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}