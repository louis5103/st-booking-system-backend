package com.springproject.stbookingsystem.dto;


import com.springproject.stbookingsystem.entity.Seat;

public class SeatDTO {

    /**
     * 좌석 응답 DTO
     */
    public static class SeatResponse {
        private Long id;
        private String seatNumber;
        private Boolean isBooked;
        private Long performanceId;

        // 기본 생성자
        public SeatResponse() {}

        // Entity로부터 DTO 생성
        public static SeatResponse from(Seat seat) {
            SeatResponse response = new SeatResponse();
            response.setId(seat.getId());
            response.setSeatNumber(seat.getSeatNumber());
            response.setIsBooked(seat.getIsBooked());
            response.setPerformanceId(seat.getPerformance().getId());
            return response;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }

        public Boolean getIsBooked() {
            return isBooked;
        }

        public void setIsBooked(Boolean isBooked) {
            this.isBooked = isBooked;
        }

        public Long getPerformanceId() {
            return performanceId;
        }

        public void setPerformanceId(Long performanceId) {
            this.performanceId = performanceId;
        }
    }
}