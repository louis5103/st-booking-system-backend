package com.springproject.stbookingsystem.sevice;


import com.springproject.stbookingsystem.dto.BookingDTO;
import com.springproject.stbookingsystem.entity.Booking;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.entity.User;
import com.springproject.stbookingsystem.repository.BookingRepository;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private AuthService authService;

    /**
     * 예매 생성
     */
    public BookingDTO.BookingResponse createBooking(BookingDTO.BookingRequest request) {
        User currentUser = authService.getCurrentUser();

        // 공연 정보 확인
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        // 좌석 정보 확인
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다"));

        // 좌석이 해당 공연의 좌석인지 확인
        if (!seat.getPerformance().getId().equals(performance.getId())) {
            throw new RuntimeException("해당 공연의 좌석이 아닙니다");
        }

        // 좌석이 이미 예매되었는지 확인 (동시성 처리)
        if (seat.getIsBooked()) {
            throw new RuntimeException("이미 예매된 좌석입니다");
        }

        // 공연 시간이 이미 지났는지 확인
        if (performance.getPerformanceDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("이미 지난 공연입니다");
        }

        // 같은 사용자가 같은 공연을 중복 예매하는지 확인 (선택사항)
        List<Booking> existingBookings = bookingRepository.findByUserAndPerformance(currentUser, performance);
        long confirmedBookings = existingBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        if (confirmedBookings >= 4) { // 한 공연당 최대 4매까지
            throw new RuntimeException("한 공연당 최대 4매까지 예매 가능합니다");
        }

        // 좌석 예매 처리
        seat.book();
        seatRepository.save(seat);

        // 예매 정보 생성
        Booking booking = new Booking(currentUser, performance, seat);
        Booking savedBooking = bookingRepository.save(booking);

        return BookingDTO.BookingResponse.from(savedBooking);
    }

    /**
     * 예매 취소
     */
    public void cancelBooking(Long bookingId) {
        User currentUser = authService.getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("예매를 찾을 수 없습니다"));

        // 본인의 예매인지 확인
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("본인의 예매만 취소할 수 있습니다");
        }

        // 취소 가능한지 확인
        if (!booking.canCancel()) {
            throw new RuntimeException("취소할 수 없는 예매입니다. (공연 24시간 전까지만 취소 가능)");
        }

        // 예매 취소 처리
        booking.cancel();
        bookingRepository.save(booking);

        // 좌석 상태 변경
        Seat seat = booking.getSeat();
        seat.cancel();
        seatRepository.save(seat);
    }

    /**
     * 사용자의 모든 예매 조회
     */
    @Transactional(readOnly = true)
    public List<BookingDTO.BookingResponse> getMyBookings() {
        User currentUser = authService.getCurrentUser();

        return bookingRepository.findByUserOrderByBookingDateDesc(currentUser)
                .stream()
                .map(BookingDTO.BookingResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 예매 상세 조회
     */
    @Transactional(readOnly = true)
    public BookingDTO.BookingResponse getBookingById(Long bookingId) {
        User currentUser = authService.getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("예매를 찾을 수 없습니다"));

        // 본인의 예매인지 확인 (관리자는 모든 예매 조회 가능)
        if (!booking.getUser().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != User.Role.ROLE_ADMIN) {
            throw new RuntimeException("접근 권한이 없습니다");
        }

        return BookingDTO.BookingResponse.from(booking);
    }

    /**
     * 특정 공연의 모든 예매 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public List<BookingDTO.BookingResponse> getBookingsByPerformance(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        return bookingRepository.findByPerformanceOrderByBookingDateDesc(performance)
                .stream()
                .map(BookingDTO.BookingResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 취소 가능한 예매 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BookingDTO.BookingResponse> getCancellableBookings() {
        User currentUser = authService.getCurrentUser();
        LocalDateTime twentyFourHoursLater = LocalDateTime.now().plusHours(24);

        return bookingRepository.findCancellableBookingsByUserId(currentUser.getId(), twentyFourHoursLater)
                .stream()
                .map(BookingDTO.BookingResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 예매 통계 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public BookingStatistics getBookingStatistics(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        Long totalBookings = bookingRepository.countConfirmedBookingsByPerformanceId(performanceId);
        Long totalRevenue = totalBookings * performance.getPrice();

        return new BookingStatistics(
                totalBookings,
                totalRevenue,
                performance.getTotalSeats() - totalBookings.intValue(),
                performance.getPrice()
        );
    }

    /**
     * 예매 통계를 위한 내부 클래스
     */
    public static class BookingStatistics {
        private Long totalBookings;
        private Long totalRevenue;
        private Integer availableSeats;
        private Integer ticketPrice;

        public BookingStatistics(Long totalBookings, Long totalRevenue,
                                 Integer availableSeats, Integer ticketPrice) {
            this.totalBookings = totalBookings;
            this.totalRevenue = totalRevenue;
            this.availableSeats = availableSeats;
            this.ticketPrice = ticketPrice;
        }

        // Getters
        public Long getTotalBookings() {
            return totalBookings;
        }

        public Long getTotalRevenue() {
            return totalRevenue;
        }

        public Integer getAvailableSeats() {
            return availableSeats;
        }

        public Integer getTicketPrice() {
            return ticketPrice;
        }

        public Long getExpectedRevenue() {
            return totalRevenue + (availableSeats * ticketPrice);
        }
    }
}