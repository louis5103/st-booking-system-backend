package com.springproject.stbookingsystem.exception.custom;

/**
 * 이미 취소된 예매 예외
 */
public class BookingAlreadyCancelledException extends BusinessException {
    public BookingAlreadyCancelledException() {
        super("BOOKING_ALREADY_CANCELLED", "이미 취소된 예매입니다");
    }
}
