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

/**
 * 지난 공연 예외
 */
public class PastPerformanceException extends BusinessException {
    public PastPerformanceException() {
        super("PAST_PERFORMANCE", "이미 지난 공연입니다");
    }
}

/**
 * 매진된 공연 예외
 */
public class SoldOutPerformanceException extends BusinessException {
    public SoldOutPerformanceException() {
        super("SOLD_OUT_PERFORMANCE", "매진된 공연입니다");
    }
}

/**
 * 공연 삭제 불가 예외
 */
public class PerformanceCannotBeDeletedException extends BusinessException {
    public PerformanceCannotBeDeletedException(String reason) {
        super("PERFORMANCE_CANNOT_BE_DELETED", "공연을 삭제할 수 없습니다: " + reason);
    }
}
