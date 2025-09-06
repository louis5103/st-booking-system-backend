package com.springproject.stbookingsystem.exception.custom;

/**
 * 매진된 공연 예외
 */
public class SoldOutPerformanceException extends BusinessException {
    public SoldOutPerformanceException() {
        super("SOLD_OUT_PERFORMANCE", "매진된 공연입니다");
    }
}
