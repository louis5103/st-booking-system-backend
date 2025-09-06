package com.springproject.stbookingsystem.exception.custom;

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
