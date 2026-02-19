package com.project.kisan_setu.exception;

import com.project.kisan_setu.dto.ApiErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUserException(UserException ex, HttpServletRequest request) {
        ApiErrorResponseDto response = new ApiErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                null,
                request.getRequestURI(),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex,
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGenericException(Exception ex, HttpServletRequest request) {
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
