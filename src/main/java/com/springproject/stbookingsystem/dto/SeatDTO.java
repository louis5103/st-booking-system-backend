package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SeatDTO {

    /**
     * 좌석 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatResponse {
        private Long id;
        private String seatNumber;
        private Boolean isBooked;
        private Long performanceId;
        private String rowInfo;
        private String numberInfo;
        private String statusText;

        // Entity로부터 DTO 생성
        public static SeatResponse from(Seat seat) {
            return SeatResponse.builder()
                    .id(seat.getId())
                    .seatNumber(seat.getSeatNumber())
                    .isBooked(seat.getIsBooked())
                    .performanceId(seat.getPerformance().getId())
                    .rowInfo(seat.getRowInfo())
                    .numberInfo(seat.getNumberInfo())
                    .statusText(seat.getStatusText())
                    .build();
        }
    }
}
