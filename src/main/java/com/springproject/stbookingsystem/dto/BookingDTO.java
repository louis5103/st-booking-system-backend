package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.Booking;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class BookingDTO {

    /**
     * 예매 요청 DTO
     */
    public static class BookingRequest {
        @NotNull(message = "공연 ID는 필수입니다")
        @Positive(message = "공연 ID는 양수여야 합니다")
        private Long performanceId;

        @NotNull(message = "좌석 ID는 필수입니다")
        @Positive(message = "좌석 ID는 양수여야 합니다")
        private Long seatId;

        // 기본 생성자
        public BookingRequest() {}

        // 생성자
        public BookingRequest(Long performanceId, Long seatId) {
            this.performanceId = performanceId;
            this.seatId = seatId;
        }

        // Getters and Setters
        public Long getPerformanceId() {
            return performanceId;
        }

        public void setPerformanceId(Long performanceId) {
            this.performanceId = performanceId;
        }

        public Long getSeatId() {
            return seatId;
        }

        public void setSeatId(Long seatId) {
            this.seatId = seatId;
        }
    }

    /**
     * 예매 응답 DTO
     */
    public static class BookingResponse {
        private Long id;
        private PerformanceInfo performance;
        private SeatInfo seat;
        private String status;
        private LocalDateTime bookingDate;
        private LocalDateTime cancelledDate;

        // 기본 생성자
        public BookingResponse() {}

        // Entity로부터 DTO 생성
        public static BookingResponse from(Booking booking) {
            BookingResponse response = new BookingResponse();
            response.setId(booking.getId());
            response.setPerformance(PerformanceInfo.from(booking.getPerformance()));
            response.setSeat(SeatInfo.from(booking.getSeat()));
            response.setStatus(booking.getStatus().name());
            response.setBookingDate(booking.getBookingDate());
            response.setCancelledDate(booking.getCancelledDate());
            return response;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public PerformanceInfo getPerformance() {
            return performance;
        }

        public void setPerformance(PerformanceInfo performance) {
            this.performance = performance;
        }

        public SeatInfo getSeat() {
            return seat;
        }

        public void setSeat(SeatInfo seat) {
            this.seat = seat;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getBookingDate() {
            return bookingDate;
        }

        public void setBookingDate(LocalDateTime bookingDate) {
            this.bookingDate = bookingDate;
        }

        public LocalDateTime getCancelledDate() {
            return cancelledDate;
        }

        public void setCancelledDate(LocalDateTime cancelledDate) {
            this.cancelledDate = cancelledDate;
        }
    }

    /**
     * 예매 내 공연 정보 DTO
     */
    public static class PerformanceInfo {
        private Long id;
        private String title;
        private String venue;
        private LocalDateTime performanceDate;
        private Integer price;

        public static PerformanceInfo from(Performance performance) {
            PerformanceInfo info = new PerformanceInfo();
            info.setId(performance.getId());
            info.setTitle(performance.getTitle());
            info.setVenue(performance.getVenue());
            info.setPerformanceDate(performance.getPerformanceDate());
            info.setPrice(performance.getPrice());
            return info;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public LocalDateTime getPerformanceDate() {
            return performanceDate;
        }

        public void setPerformanceDate(LocalDateTime performanceDate) {
            this.performanceDate = performanceDate;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }
    }

    /**
     * 예매 내 좌석 정보 DTO
     */
    public static class SeatInfo {
        private Long id;
        private String seatNumber;

        public static SeatInfo from(Seat seat) {
            SeatInfo info = new SeatInfo();
            info.setId(seat.getId());
            info.setSeatNumber(seat.getSeatNumber());
            return info;
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
    }
}