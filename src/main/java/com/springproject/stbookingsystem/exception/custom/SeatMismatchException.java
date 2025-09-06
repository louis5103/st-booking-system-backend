package com.springproject.stbookingsystem.exception.custom;

/**
 * 좌석 불일치 예외
 */
public class SeatMismatchException extends BusinessException {
    public SeatMismatchException() {
        super("SEAT_MISMATCH", "해당 공연의 좌석이 아닙니다");
    }
}
