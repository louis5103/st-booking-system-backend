package com.springproject.stbookingsystem.exception.custom;

/**
 * 예매 관련 예외
 */
public class BookingNotFoundException extends BusinessException {
    public BookingNotFoundException() {
        super("BOOKING_NOT_FOUND", "예매를 찾을 수 없습니다");
    }
    
    public BookingNotFoundException(Long bookingId) {
        super("BOOKING_NOT_FOUND", "예매를 찾을 수 없습니다. ID: " + bookingId);
    }
}
