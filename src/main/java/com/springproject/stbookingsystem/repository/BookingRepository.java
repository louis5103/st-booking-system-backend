package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.Booking;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * 특정 사용자의 모든 예매 조회 (예매일 최신순) - 페이징
     */
    Page<Booking> findByUserOrderByBookingDateDesc(User user, Pageable pageable);
    
    /**
     * 특정 사용자의 모든 예매 조회 (예매일 최신순) - 리스트
     */
    List<Booking> findByUserOrderByBookingDateDesc(User user);

    /**
     * 사용자 ID로 모든 예매 조회 - 페이징
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.performance JOIN FETCH b.seat " +
           "WHERE b.user.id = :userId ORDER BY b.bookingDate DESC")
    Page<Booking> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 공연의 모든 예매 조회 - 페이징
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.seat " +
           "WHERE b.performance = :performance ORDER BY b.bookingDate DESC")
    Page<Booking> findByPerformanceWithDetails(Performance performance, Pageable pageable);

    /**
     * 특정 사용자의 상태별 예매 조회
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.performance JOIN FETCH b.seat " +
           "WHERE b.user = :user AND b.status = :status ORDER BY b.bookingDate DESC")
    List<Booking> findByUserAndStatusWithDetails(@Param("user") User user, 
                                                @Param("status") Booking.BookingStatus status);

    /**
     * 특정 공연의 확정된 예매 수 조회
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.performance.id = :performanceId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByPerformanceId(@Param("performanceId") Long performanceId);

    /**
     * 특정 사용자의 확정된 예매 수 조회
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자의 특정 공연 예매 조회 (중복 예매 확인)
     */
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.performance = :performance AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByUserAndPerformance(@Param("user") User user, 
                                                           @Param("performance") Performance performance);

    /**
     * 특정 사용자와 공연의 모든 예매 조회
     */
    List<Booking> findByUserAndPerformance(User user, Performance performance);

    /**
     * 특정 공연의 모든 예매 조회 (예매일 최신순) - 리스트
     */
    List<Booking> findByPerformanceOrderByBookingDateDesc(Performance performance);

    /**
     * 취소 가능한 예매 조회 (공연 24시간 전, 확정 상태)
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.performance JOIN FETCH b.seat " +
           "WHERE b.user.id = :userId AND b.status = 'CONFIRMED' " +
           "AND b.performance.performanceDate > :deadline ORDER BY b.bookingDate DESC")
    List<Booking> findCancellableBookingsByUserId(@Param("userId") Long userId,
                                                  @Param("deadline") LocalDateTime deadline);

    /**
     * 좌석 ID로 활성 예매 조회
     */
    @Query("SELECT b FROM Booking b WHERE b.seat.id = :seatId AND b.status = 'CONFIRMED'")
    Optional<Booking> findActiveBySeatId(@Param("seatId") Long seatId);

    /**
     * 특정 기간의 예매 통계 조회
     */
    @Query("SELECT COUNT(b), SUM(b.performance.price), b.performance.id " +
           "FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate " +
           "AND b.status = 'CONFIRMED' GROUP BY b.performance.id")
    List<Object[]> findBookingStatisticsByPeriod(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * 인기 공연 조회 (예매 수 기준)
     */
    @Query("SELECT b.performance, COUNT(b) as bookingCount " +
           "FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "GROUP BY b.performance ORDER BY bookingCount DESC")
    List<Object[]> findPopularPerformances(Pageable pageable);

    /**
     * 특정 사용자의 최근 예매 조회
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.performance " +
           "WHERE b.user.id = :userId ORDER BY b.bookingDate DESC")
    List<Booking> findRecentBookingsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 공연별 일일 예매 수 조회
     */
    @Query("SELECT DATE(b.bookingDate), COUNT(b) " +
           "FROM Booking b WHERE b.performance.id = :performanceId " +
           "AND b.status = 'CONFIRMED' " +
           "GROUP BY DATE(b.bookingDate) ORDER BY DATE(b.bookingDate)")
    List<Object[]> findDailyBookingsByPerformance(@Param("performanceId") Long performanceId);
}
