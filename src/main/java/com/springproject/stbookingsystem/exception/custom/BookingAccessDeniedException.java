package com.springproject.stbookingsystem.exception.custom;

/**
 * 예매 권한 없음 예외
 */
public class BookingAccessDeniedException extends BusinessException {
    public BookingAccessDeniedException() {
        super("BOOKING_ACCESS_DENIED", "본인의 예매만 접근할 수 있습니다");
    }
}
