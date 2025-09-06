package com.springproject.stbookingsystem.exception;

import com.springproject.stbookingsystem.common.ApiResponse;
import com.springproject.stbookingsystem.exception.custom.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.warn("Business exception occurred: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 유효성 검증 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.error(
                "VALIDATION_FAILED", 
                "입력값 검증에 실패했습니다", 
                errors
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 인증 실패 예외 처리
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex) {
        
        log.warn("Authentication failed: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
                "AUTHENTICATION_FAILED", 
                "인증에 실패했습니다"
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 권한 부족 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
                "ACCESS_DENIED", 
                "접근 권한이 없습니다"
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
                "INVALID_ARGUMENT", 
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * IllegalStateException 처리
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(
            IllegalStateException ex) {
        
        log.warn("Illegal state: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
                "INVALID_STATE", 
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        ApiResponse<Void> response = ApiResponse.error(
                "RUNTIME_ERROR", 
                "요청 처리 중 오류가 발생했습니다"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 모든 예외의 최종 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ApiResponse<Void> response = ApiResponse.error(
                "INTERNAL_SERVER_ERROR", 
                "서버 내부 오류가 발생했습니다"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
