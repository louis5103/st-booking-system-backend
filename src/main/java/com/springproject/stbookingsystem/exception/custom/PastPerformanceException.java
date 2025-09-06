package com.springproject.stbookingsystem.exception.custom;

/**
 * 지난 공연 예외
 */
public class PastPerformanceException extends BusinessException {
    public PastPerformanceException() {
        super("PAST_PERFORMANCE", "이미 지난 공연입니다");
    }
}
