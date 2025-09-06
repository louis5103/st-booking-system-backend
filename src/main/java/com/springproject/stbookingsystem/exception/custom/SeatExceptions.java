package com.springproject.stbookingsystem.exception.custom;

/**
 * 좌석 관련 예외
 */
public class SeatNotFoundException extends BusinessException {
    public SeatNotFoundException() {
        super("SEAT_NOT_FOUND", "좌석을 찾을 수 없습니다");
    }
    
    public SeatNotFoundException(Long seatId) {
        super("SEAT_NOT_FOUND", "좌석을 찾을 수 없습니다. ID: " + seatId);
    }
}

/**
 * 이미 예매된 좌석 예외
 */
public class SeatAlreadyBookedException extends BusinessException {
    public SeatAlreadyBookedException(String seatNumber) {
        super("SEAT_ALREADY_BOOKED", "이미 예매된 좌석입니다: " + seatNumber);
    }
}

/**
 * 좌석 불일치 예외
 */
public class SeatMismatchException extends BusinessException {
    public SeatMismatchException() {
        super("SEAT_MISMATCH", "해당 공연의 좌석이 아닙니다");
    }
}
