package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.SeatLayout;
import com.springproject.stbookingsystem.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatLayoutRepository extends JpaRepository<SeatLayout, Long> {

    /**
     * 특정 공연장의 모든 좌석 배치 조회 (위치 순)
     */
    List<SeatLayout> findByVenueOrderByRowNumberAscSeatNumberAsc(Venue venue);

    /**
     * 특정 공연장의 활성 좌석 배치만 조회
     */
    List<SeatLayout> findByVenueAndIsActiveTrueOrderByRowNumberAscSeatNumberAsc(Venue venue);

    /**
     * 공연장 ID로 좌석 배치 조회
     */
    List<SeatLayout> findByVenueIdOrderByRowNumberAscSeatNumberAsc(Long venueId);

    /**
     * 특정 공연장의 특정 위치 좌석 조회
     */
    Optional<SeatLayout> findByVenueAndRowNumberAndSeatNumber(Venue venue, Integer rowNumber, Integer seatNumber);

    /**
     * 특정 공연장의 특정 좌석 타입 조회
     */
    List<SeatLayout> findByVenueAndSeatType(Venue venue, SeatLayout.SeatType seatType);

    /**
     * 특정 공연장의 좌석 타입별 개수
     */
    @Query("SELECT sl.seatType, COUNT(sl) FROM SeatLayout sl " +
           "WHERE sl.venue = :venue AND sl.isActive = true " +
           "GROUP BY sl.seatType")
    List<Object[]> countSeatsByTypeForVenue(@Param("venue") Venue venue);

    /**
     * 특정 공연장의 예매 가능한 좌석 수
     */
    @Query("SELECT COUNT(sl) FROM SeatLayout sl " +
           "WHERE sl.venue = :venue AND sl.isActive = true " +
           "AND sl.seatType IN ('REGULAR', 'VIP', 'PREMIUM', 'WHEELCHAIR')")
    Long countBookableSeatsForVenue(@Param("venue") Venue venue);

    /**
     * 특정 공연장의 특정 행의 좌석들 조회
     */
    List<SeatLayout> findByVenueAndRowNumberOrderBySeatNumberAsc(Venue venue, Integer rowNumber);

    /**
     * 좌석 레이블로 검색
     */
    Optional<SeatLayout> findByVenueAndSeatLabel(Venue venue, String seatLabel);

    /**
     * 공연장별 좌석 배치 통계
     */
    @Query("SELECT v.name, " +
           "COUNT(sl), " +
           "COUNT(CASE WHEN sl.isActive = true THEN 1 END), " +
           "COUNT(CASE WHEN sl.seatType IN ('REGULAR', 'VIP', 'PREMIUM', 'WHEELCHAIR') AND sl.isActive = true THEN 1 END) " +
           "FROM Venue v " +
           "LEFT JOIN v.seatLayouts sl " +
           "GROUP BY v.id, v.name " +
           "ORDER BY v.name")
    List<Object[]> getVenueSeatStatistics();

    /**
     * 특정 공연장에서 중복되는 좌석 배치가 있는지 확인
     */
    boolean existsByVenueAndRowNumberAndSeatNumber(Venue venue, Integer rowNumber, Integer seatNumber);

    /**
     * 특정 공연장의 최대 행 번호 조회
     */
    @Query("SELECT MAX(sl.rowNumber) FROM SeatLayout sl WHERE sl.venue = :venue")
    Integer findMaxRowNumberByVenue(@Param("venue") Venue venue);

    /**
     * 특정 공연장의 특정 행에서 최대 좌석 번호 조회
     */
    @Query("SELECT MAX(sl.seatNumber) FROM SeatLayout sl " +
           "WHERE sl.venue = :venue AND sl.rowNumber = :rowNumber")
    Integer findMaxSeatNumberByVenueAndRow(@Param("venue") Venue venue, @Param("rowNumber") Integer rowNumber);
}
