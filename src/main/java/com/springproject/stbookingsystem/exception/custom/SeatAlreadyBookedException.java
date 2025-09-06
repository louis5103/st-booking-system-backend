package com.springproject.stbookingsystem.exception.custom;

/**
 * 이미 예매된 좌석 예외
 */
public class SeatAlreadyBookedException extends BusinessException {
    public SeatAlreadyBookedException(String seatNumber) {
        super("SEAT_ALREADY_BOOKED", "이미 예매된 좌석입니다: " + seatNumber);
    }
}
