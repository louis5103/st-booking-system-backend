package com.springproject.stbookingsystem.exception.custom;

/**
 * 예매 한도 초과 예외
 */
public class BookingLimitExceededException extends BusinessException {
    public BookingLimitExceededException(int limit) {
        super("BOOKING_LIMIT_EXCEEDED", "한 공연당 최대 " + limit + "매까지 예매 가능합니다");
    }
}
