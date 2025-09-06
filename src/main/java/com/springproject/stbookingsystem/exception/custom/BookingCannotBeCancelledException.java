package com.springproject.stbookingsystem.exception.custom;

/**
 * 예매 취소 불가 예외
 */
public class BookingCannotBeCancelledException extends BusinessException {
    public BookingCannotBeCancelledException(String reason) {
        super("BOOKING_CANNOT_BE_CANCELLED", "예매를 취소할 수 없습니다: " + reason);
    }
}
