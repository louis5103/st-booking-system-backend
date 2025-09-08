package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.SeatLayout;
import com.springproject.stbookingsystem.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatLayoutRepository extends JpaRepository<SeatLayout, Long> {

    /**
     * 특정 공연장의 모든 좌석 배치 조회 (ID 순)
     */
    List<SeatLayout> findByVenueOrderByIdAsc(Venue venue);

    /**
     * 특정 공연장의 좌석 배치 삭제
     */
    @Modifying
    void deleteByVenue(Venue venue);

    /**
     * 특정 공연장의 모든 좌석 배치 조회 (위치 순)
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue ORDER BY sl.rowNumber ASC, sl.seatNumber ASC")
    List<SeatLayout> findByVenueOrderByRowNumberAscSeatNumberAsc(@Param("venue") Venue venue);

    /**
     * 특정 공연장의 활성 좌석 배치만 조회
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue AND sl.isActive = true ORDER BY sl.rowNumber ASC, sl.seatNumber ASC")
    List<SeatLayout> findByVenueAndIsActiveTrueOrderByRowNumberAscSeatNumberAsc(@Param("venue") Venue venue);

    /**
     * 공연장 ID로 좌석 배치 조회
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue.id = :venueId ORDER BY sl.rowNumber ASC, sl.seatNumber ASC")
    List<SeatLayout> findByVenueIdOrderByRowNumberAscSeatNumberAsc(@Param("venueId") Long venueId);

    /**
     * 특정 공연장의 특정 위치 좌석 조회
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue AND sl.rowNumber = :rowNumber AND sl.seatNumber = :seatNumber")
    Optional<SeatLayout> findByVenueAndRowNumberAndSeatNumber(@Param("venue") Venue venue, @Param("rowNumber") Integer rowNumber, @Param("seatNumber") Integer seatNumber);

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
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue AND sl.rowNumber = :rowNumber ORDER BY sl.seatNumber ASC")
    List<SeatLayout> findByVenueAndRowNumberOrderBySeatNumberAsc(@Param("venue") Venue venue, @Param("rowNumber") Integer rowNumber);

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
    @Query("SELECT CASE WHEN COUNT(sl) > 0 THEN true ELSE false END FROM SeatLayout sl " +
           "WHERE sl.venue = :venue AND sl.rowNumber = :rowNumber AND sl.seatNumber = :seatNumber")
    boolean existsByVenueAndRowNumberAndSeatNumber(@Param("venue") Venue venue, @Param("rowNumber") Integer rowNumber, @Param("seatNumber") Integer seatNumber);

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

    /**
     * 특정 공연장의 특정 섹션 좌석들 조회
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue AND sl.sectionId = :sectionId ORDER BY sl.rowNumber ASC, sl.seatNumber ASC")
    List<SeatLayout> findByVenueAndSectionId(@Param("venue") Venue venue, @Param("sectionId") Integer sectionId);

    /**
     * 특정 공연장의 모든 섹션 ID 조회
     */
    @Query("SELECT DISTINCT sl.sectionId FROM SeatLayout sl WHERE sl.venue = :venue AND sl.sectionId IS NOT NULL ORDER BY sl.sectionId")
    List<Integer> findDistinctSectionIdsByVenue(@Param("venue") Venue venue);

    /**
     * 특정 공연장의 섹션별 좌석 수 조회
     */
    @Query("SELECT sl.sectionId, COUNT(sl) FROM SeatLayout sl " +
           "WHERE sl.venue = :venue AND sl.sectionId IS NOT NULL " +
           "GROUP BY sl.sectionId ORDER BY sl.sectionId")
    List<Object[]> countSeatsBySectionForVenue(@Param("venue") Venue venue);

    /**
     * 특정 공연장의 섹션별 수익 조회
     */
    @Query("SELECT sl.sectionId, SUM(COALESCE(sl.price, 0)) FROM SeatLayout sl " +
           "WHERE sl.venue = :venue AND sl.sectionId IS NOT NULL " +
           "GROUP BY sl.sectionId ORDER BY sl.sectionId")
    List<Object[]> sumRevenueBySectionForVenue(@Param("venue") Venue venue);

    /**
     * X, Y 좌표로 좌석 조회 (유연한 배치용)
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue AND sl.xPosition = :xPosition AND sl.yPosition = :yPosition")
    List<SeatLayout> findByVenueAndPosition(@Param("venue") Venue venue, @Param("xPosition") Integer xPosition, @Param("yPosition") Integer yPosition);

    /**
     * 특정 공연장의 좌석을 섹션별로 그룹화하여 조회
     */
    @Query("SELECT sl FROM SeatLayout sl WHERE sl.venue = :venue ORDER BY sl.sectionId ASC, sl.seatLabel ASC")
    List<SeatLayout> findByVenueOrderBySectionIdAscSeatLabelAsc(@Param("venue") Venue venue);

    /**
     * seats 테이블에서 특정 seatLayout을 참조하는 레코드들의 seat_layout_id를 null로 설정
     */
    @Modifying
    @Query("UPDATE Seat s SET s.seatLayout = null WHERE s.seatLayout.id = :seatLayoutId")
    void clearSeatLayoutReferences(@Param("seatLayoutId") Long seatLayoutId);
}