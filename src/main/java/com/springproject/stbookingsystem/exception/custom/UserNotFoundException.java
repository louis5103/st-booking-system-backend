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
