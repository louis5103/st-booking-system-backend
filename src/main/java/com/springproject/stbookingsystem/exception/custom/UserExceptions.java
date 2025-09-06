package com.springproject.stbookingsystem.exception.custom;

/**
 * 사용자 관련 예외
 */
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND", "사용자를 찾을 수 없습니다");
    }
    
    public UserNotFoundException(String message) {
        super("USER_NOT_FOUND", message);
    }
    
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "사용자를 찾을 수 없습니다. ID: " + userId);
    }
}

/**
 * 이메일 중복 예외
 */
public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다: " + email);
    }
}

/**
 * 전화번호 중복 예외
 */
public class PhoneAlreadyExistsException extends BusinessException {
    public PhoneAlreadyExistsException(String phone) {
        super("PHONE_ALREADY_EXISTS", "이미 존재하는 전화번호입니다: " + phone);
    }
}

/**
 * 인증 실패 예외
 */
public class AuthenticationFailedException extends BusinessException {
    public AuthenticationFailedException() {
        super("AUTHENTICATION_FAILED", "인증에 실패했습니다");
    }
    
    public AuthenticationFailedException(String message) {
        super("AUTHENTICATION_FAILED", message);
    }
}

/**
 * 권한 부족 예외
 */
public class AccessDeniedException extends BusinessException {
    public AccessDeniedException() {
        super("ACCESS_DENIED", "접근 권한이 없습니다");
    }
    
    public AccessDeniedException(String message) {
        super("ACCESS_DENIED", message);
    }
}
