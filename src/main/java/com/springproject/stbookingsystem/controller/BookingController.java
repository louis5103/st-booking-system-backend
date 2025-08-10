package com.springproject.stbookingsystem.controller;

import com.springproject.stbookingsystem.dto.BookingDTO;
import com.springproject.stbookingsystem.sevice.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 예매 생성
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDTO.BookingRequest request) {
        try {
            BookingDTO.BookingResponse booking = bookingService.createBooking(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매 실패: " + e.getMessage()));
        }
    }

    /**
     * 내 예매 목록 조회
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyBookings() {
        try {
            List<BookingDTO.BookingResponse> bookings = bookingService.getMyBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매 목록 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 예매 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            BookingDTO.BookingResponse booking = bookingService.getBookingById(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매 정보 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 예매 취소
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok(new SuccessResponse("예매가 성공적으로 취소되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매 취소 실패: " + e.getMessage()));
        }
    }

    /**
     * 취소 가능한 예매 목록 조회
     */
    @GetMapping("/cancellable")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCancellableBookings() {
        try {
            List<BookingDTO.BookingResponse> bookings = bookingService.getCancellableBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("취소 가능한 예매 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 공연의 예매 목록 조회 (관리자용)
     */
    @GetMapping("/performance/{performanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBookingsByPerformance(@PathVariable Long performanceId) {
        try {
            List<BookingDTO.BookingResponse> bookings =
                    bookingService.getBookingsByPerformance(performanceId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연별 예매 목록 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 예매 통계 조회 (관리자용)
     */
    @GetMapping("/statistics/{performanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBookingStatistics(@PathVariable Long performanceId) {
        try {
            BookingService.BookingStatistics statistics =
                    bookingService.getBookingStatistics(performanceId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("예매 통계 조회 실패: " + e.getMessage()));
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

    static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}