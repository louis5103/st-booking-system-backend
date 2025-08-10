package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.Booking;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * 특정 사용자의 모든 예매 조회 (예매일 최신순)
     */
    List<Booking> findByUserOrderByBookingDateDesc(User user);

    /**
     * 사용자 ID로 모든 예매 조회
     */
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

    /**
     * 특정 공연의 모든 예매 조회
     */
    List<Booking> findByPerformanceOrderByBookingDateDesc(Performance performance);

    /**
     * 특정 사용자의 확정된 예매만 조회
     */
    List<Booking> findByUserAndStatusOrderByBookingDateDesc(User user, Booking.BookingStatus status);

    /**
     * 특정 공연의 확정된 예매만 조회
     */
    List<Booking> findByPerformanceAndStatusOrderByBookingDateDesc(Performance performance, Booking.BookingStatus status);

    /**
     * 특정 좌석의 예매 정보 조회
     */
    @Query("SELECT b FROM Booking b WHERE b.seat.id = :seatId")
    List<Booking> findBySeatId(@Param("seatId") Long seatId);

    /**
     * 특정 기간 내 예매 조회
     */
    List<Booking> findByBookingDateBetweenOrderByBookingDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 사용자의 특정 공연 예매 조회
     */
    List<Booking> findByUserAndPerformance(User user, Performance performance);

    /**
     * 특정 공연의 총 예매 수 (확정된 예매만)
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.performance.id = :performanceId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByPerformanceId(@Param("performanceId") Long performanceId);

    /**
     * 특정 사용자의 총 예매 수
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByUserId(@Param("userId") Long userId);

    /**
     * 취소 가능한 예매 조회 (공연 24시간 전, 확정 상태)
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = 'CONFIRMED' " +
            "AND b.performance.performanceDate > :twentyFourHoursLater ORDER BY b.bookingDate DESC")
    List<Booking> findCancellableBookingsByUserId(@Param("userId") Long userId,
                                                  @Param("twentyFourHoursLater") LocalDateTime twentyFourHoursLater);
}