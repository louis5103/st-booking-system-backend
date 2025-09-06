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

/**
 * 예매 한도 초과 예외
 */
public class BookingLimitExceededException extends BusinessException {
    public BookingLimitExceededException(int limit) {
        super("BOOKING_LIMIT_EXCEEDED", "한 공연당 최대 " + limit + "매까지 예매 가능합니다");
    }
}

/**
 * 예매 취소 불가 예외
 */
public class BookingCannotBeCancelledException extends BusinessException {
    public BookingCannotBeCancelledException(String reason) {
        super("BOOKING_CANNOT_BE_CANCELLED", "예매를 취소할 수 없습니다: " + reason);
    }
}

/**
 * 이미 취소된 예매 예외
 */
public class BookingAlreadyCancelledException extends BusinessException {
    public BookingAlreadyCancelledException() {
        super("BOOKING_ALREADY_CANCELLED", "이미 취소된 예매입니다");
    }
}

/**
 * 예매 권한 없음 예외
 */
public class BookingAccessDeniedException extends BusinessException {
    public BookingAccessDeniedException() {
        super("BOOKING_ACCESS_DENIED", "본인의 예매만 접근할 수 있습니다");
    }
}
