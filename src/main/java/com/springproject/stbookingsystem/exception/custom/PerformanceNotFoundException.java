package com.springproject.stbookingsystem.exception.custom;

/**
 * 공연 관련 예외
 */
public class PerformanceNotFoundException extends BusinessException {
    public PerformanceNotFoundException() {
        super("PERFORMANCE_NOT_FOUND", "공연을 찾을 수 없습니다");
    }
    
    public PerformanceNotFoundException(Long performanceId) {
        super("PERFORMANCE_NOT_FOUND", "공연을 찾을 수 없습니다. ID: " + performanceId);
    }
}
