package com.springproject.stbookingsystem.exception.custom;

/**
 * 공연 삭제 불가 예외
 */
public class PerformanceCannotBeDeletedException extends BusinessException {
    public PerformanceCannotBeDeletedException(String reason) {
        super("PERFORMANCE_CANNOT_BE_DELETED", "공연을 삭제할 수 없습니다: " + reason);
    }
}
