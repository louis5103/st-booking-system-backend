package com.springproject.stbookingsystem.controller;


import com.springproject.stbookingsystem.dto.SeatDTO;
import com.springproject.stbookingsystem.sevice.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SeatController {

    @Autowired
    private SeatService seatService;

    /**
     * 특정 공연의 모든 좌석 조회
     */
    @GetMapping("/performances/{performanceId}/seats")
    public ResponseEntity<?> getSeatsByPerformance(@PathVariable Long performanceId) {
        try {
            List<SeatDTO.SeatResponse> seats = seatService.getSeatsByPerformanceId(performanceId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 공연의 예매 가능한 좌석 조회
     */
    @GetMapping("/performances/{performanceId}/seats/available")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long performanceId) {
        try {
            List<SeatDTO.SeatResponse> seats = seatService.getAvailableSeatsByPerformanceId(performanceId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매 가능한 좌석 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 공연의 예매된 좌석 조회
     */
    @GetMapping("/performances/{performanceId}/seats/booked")
    public ResponseEntity<?> getBookedSeats(@PathVariable Long performanceId) {
        try {
            List<SeatDTO.SeatResponse> seats = seatService.getBookedSeatsByPerformanceId(performanceId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매된 좌석 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 상세 정보 조회
     */
    @GetMapping("/seats/{seatId}")
    public ResponseEntity<?> getSeatById(@PathVariable Long seatId) {
        try {
            SeatDTO.SeatResponse seat = seatService.getSeatById(seatId);
            return ResponseEntity.ok(seat);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 정보 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 예매 가능 여부 확인
     */
    @GetMapping("/seats/{seatId}/available")
    public ResponseEntity<?> checkSeatAvailability(@PathVariable Long seatId) {
        try {
            boolean available = seatService.isSeatAvailable(seatId);
            return ResponseEntity.ok(new AvailabilityResponse(seatId, available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 상태 확인 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 공연의 좌석 통계 조회 (관리자용)
     */
    @GetMapping("/performances/{performanceId}/seats/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSeatStatistics(@PathVariable Long performanceId) {
        try {
            SeatService.SeatStatistics statistics = seatService.getSeatStatistics(performanceId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 통계 조회 실패: " + e.getMessage()));
        }
    }

    // 응답 클래스들
    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class AvailabilityResponse {
        private Long seatId;
        private boolean available;

        public AvailabilityResponse(Long seatId, boolean available) {
            this.seatId = seatId;
            this.available = available;
        }

        public Long getSeatId() {
            return seatId;
        }

        public boolean isAvailable() {
            return available;
        }
    }
}
