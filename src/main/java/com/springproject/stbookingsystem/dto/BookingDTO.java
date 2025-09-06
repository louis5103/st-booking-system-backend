package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.Booking;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BookingDTO {

    /**
     * 예매 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingRequest {
        @NotNull(message = "공연 ID는 필수입니다")
        @Positive(message = "공연 ID는 양수여야 합니다")
        private Long performanceId;

        @NotNull(message = "좌석 ID는 필수입니다")
        @Positive(message = "좌석 ID는 양수여야 합니다")
        private Long seatId;
    }

    /**
     * 예매 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingResponse {
        private Long id;
        private PerformanceInfo performance;
        private SeatInfo seat;
        private String status;
        private String statusText;
        private LocalDateTime bookingDate;
        private LocalDateTime cancelledDate;
        private String cancellationReason;
        private Boolean canCancel;
        private Long hoursUntilDeadline;
        private Integer price;

        // Entity로부터 DTO 생성
        public static BookingResponse from(Booking booking) {
            return BookingResponse.builder()
                    .id(booking.getId())
                    .performance(PerformanceInfo.from(booking.getPerformance()))
                    .seat(SeatInfo.from(booking.getSeat()))
                    .status(booking.getStatus().name())
                    .statusText(booking.getStatusText())
                    .bookingDate(booking.getBookingDate())
                    .cancelledDate(booking.getCancelledDate())
                    .cancellationReason(booking.getCancellationReason())
                    .canCancel(booking.canCancel())
                    .hoursUntilDeadline(booking.getHoursUntilCancellationDeadline())
                    .price(booking.getPrice())
                    .build();
        }
    }

    /**
     * 예매 내 공연 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceInfo {
        private Long id;
        private String title;
        private String venue;
        private LocalDateTime performanceDate;
        private Integer price;
        private String imageUrl;

        public static PerformanceInfo from(Performance performance) {
            return PerformanceInfo.builder()
                    .id(performance.getId())
                    .title(performance.getTitle())
                    .venue(performance.getVenue())
                    .performanceDate(performance.getPerformanceDate())
                    .price(performance.getPrice())
                    .imageUrl(performance.getImageUrl())
                    .build();
        }
    }

    /**
     * 예매 내 좌석 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatInfo {
        private Long id;
        private String seatNumber;
        private String rowInfo;
        private String numberInfo;

        public static SeatInfo from(Seat seat) {
            return SeatInfo.builder()
                    .id(seat.getId())
                    .seatNumber(seat.getSeatNumber())
                    .rowInfo(seat.getRowInfo())
                    .numberInfo(seat.getNumberInfo())
                    .build();
        }
    }
}
