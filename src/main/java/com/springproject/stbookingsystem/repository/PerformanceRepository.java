package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.Performance;
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
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    /**
     * 공연 일시순으로 정렬된 전체 공연 목록 (페이징)
     */
    Page<Performance> findAllByOrderByPerformanceDateAsc(Pageable pageable);
    
    /**
     * 공연 일시순으로 정렬된 전체 공연 목록 (리스트)
     */
    List<Performance> findAllByOrderByPerformanceDateAsc();

    /**
     * 제목 또는 공연장으로 공연 검색 (페이징)
     */
    @Query("SELECT p FROM Performance p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.venue) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.performanceDate ASC")
    Page<Performance> findByTitleOrVenueContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 제목 또는 공연장으로 공연 검색 (리스트)
     */
    @Query("SELECT p FROM Performance p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.venue) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY p.performanceDate ASC")
    List<Performance> findByTitleOrVenueContaining(@Param("keyword") String keyword);

    /**
     * 예매 가능한 공연 조회 (현재 시간 이후 + 매진되지 않은 공연)
     */
    @Query("SELECT p FROM Performance p WHERE p.performanceDate > :now " +
           "AND (SELECT COUNT(s) FROM Seat s WHERE s.performance = p AND s.isBooked = true) < p.totalSeats " +
           "ORDER BY p.performanceDate ASC")
    Page<Performance> findBookablePerformances(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * 매진된 공연 조회
     */
    @Query("SELECT p FROM Performance p WHERE " +
           "(SELECT COUNT(s) FROM Seat s WHERE s.performance = p AND s.isBooked = true) = p.totalSeats " +
           "ORDER BY p.performanceDate ASC")
    Page<Performance> findSoldOutPerformances(Pageable pageable);

    /**
     * 인기 공연 조회 (예매율 기준)
     */
    @Query("SELECT p, " +
           "(SELECT COUNT(s) FROM Seat s WHERE s.performance = p AND s.isBooked = true) * 100.0 / p.totalSeats as bookingRate " +
           "FROM Performance p WHERE p.performanceDate > :now " +
           "ORDER BY bookingRate DESC")
    List<Object[]> findPopularPerformances(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * 가격 범위로 공연 검색
     */
    @Query("SELECT p FROM Performance p WHERE p.price BETWEEN :minPrice AND :maxPrice " +
           "AND p.performanceDate > :now ORDER BY p.performanceDate ASC")
    Page<Performance> findByPriceRange(@Param("minPrice") Integer minPrice, 
                                      @Param("maxPrice") Integer maxPrice,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);

    /**
     * 특정 날짜의 공연 조회
     */
    @Query("SELECT p FROM Performance p WHERE DATE(p.performanceDate) = DATE(:date) " +
           "ORDER BY p.performanceDate ASC")
    List<Performance> findByPerformanceDate(@Param("date") LocalDateTime date);

    /**
     * 특정 기간의 공연 조회
     */
    @Query("SELECT p FROM Performance p WHERE p.performanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.performanceDate ASC")
    Page<Performance> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    /**
     * 특정 공연장의 공연 조회
     */
    @Query("SELECT p FROM Performance p WHERE LOWER(p.venue) = LOWER(:venue) " +
           "ORDER BY p.performanceDate ASC")
    Page<Performance> findByVenue(@Param("venue") String venue, Pageable pageable);

    /**
     * 공연 통계 조회 (총 수익, 예매율 등)
     */
    @Query("SELECT p.id, p.title, p.totalSeats, " +
           "(SELECT COUNT(s) FROM Seat s WHERE s.performance = p AND s.isBooked = true) as bookedSeats, " +
           "(SELECT COUNT(b) FROM Booking b WHERE b.performance = p AND b.status = 'CONFIRMED') as confirmedBookings, " +
           "p.price * (SELECT COUNT(b) FROM Booking b WHERE b.performance = p AND b.status = 'CONFIRMED') as totalRevenue " +
           "FROM Performance p WHERE p.id = :performanceId")
    Optional<Object[]> findPerformanceStatistics(@Param("performanceId") Long performanceId);

    /**
     * 임박한 공연 조회 (24시간 이내)
     */
    @Query("SELECT p FROM Performance p WHERE p.performanceDate BETWEEN :now AND :twentyFourHoursLater " +
           "ORDER BY p.performanceDate ASC")
    List<Performance> findUpcomingPerformances(@Param("now") LocalDateTime now,
                                             @Param("twentyFourHoursLater") LocalDateTime twentyFourHoursLater);

    /**
     * 월별 공연 수 조회
     */
    @Query("SELECT YEAR(p.performanceDate), MONTH(p.performanceDate), COUNT(p) " +
           "FROM Performance p WHERE p.performanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(p.performanceDate), MONTH(p.performanceDate) " +
           "ORDER BY YEAR(p.performanceDate), MONTH(p.performanceDate)")
    List<Object[]> findMonthlyPerformanceCount(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * 잔여 좌석이 적은 공연 조회 (10석 이하)
     */
    @Query("SELECT p FROM Performance p WHERE " +
           "p.totalSeats - (SELECT COUNT(s) FROM Seat s WHERE s.performance = p AND s.isBooked = true) <= 10 " +
           "AND p.performanceDate > :now ORDER BY p.performanceDate ASC")
    List<Performance> findLowAvailabilityPerformances(@Param("now") LocalDateTime now);
}
