package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * 특정 공연의 모든 좌석 조회 (좌석 번호순)
     */
    List<Seat> findByPerformanceOrderBySeatNumberAsc(Performance performance);

    /**
     * 공연 ID로 모든 좌석 조회
     */
    List<Seat> findByPerformanceIdOrderBySeatNumberAsc(Long performanceId);

    /**
     * 특정 공연의 예매 가능한 좌석 조회
     */
    List<Seat> findByPerformanceAndIsBookedFalseOrderBySeatNumberAsc(Performance performance);

    /**
     * 특정 공연의 예매된 좌석 조회
     */
    List<Seat> findByPerformanceAndIsBookedTrueOrderBySeatNumberAsc(Performance performance);

    /**
     * 공연과 좌석 번호로 좌석 찾기
     */
    Optional<Seat> findByPerformanceAndSeatNumber(Performance performance, String seatNumber);

    /**
     * 공연 ID와 좌석 번호로 좌석 찾기
     */
    Optional<Seat> findByPerformanceIdAndSeatNumber(Long performanceId, String seatNumber);

    /**
     * 특정 공연의 예매된 좌석 수 조회
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.performance.id = :performanceId AND s.isBooked = true")
    Long countBookedSeatsByPerformanceId(@Param("performanceId") Long performanceId);

    /**
     * 특정 공연의 예매 가능한 좌석 수 조회
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.performance.id = :performanceId AND s.isBooked = false")
    Long countAvailableSeatsByPerformanceId(@Param("performanceId") Long performanceId);

    /**
     * 공연과 좌석 번호 조합이 이미 존재하는지 확인
     */
    boolean existsByPerformanceAndSeatNumber(Performance performance, String seatNumber);
}