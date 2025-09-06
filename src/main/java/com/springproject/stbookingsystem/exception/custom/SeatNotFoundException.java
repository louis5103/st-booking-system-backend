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
