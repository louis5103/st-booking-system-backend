package com.springproject.stbookingsystem.exception.custom;

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
