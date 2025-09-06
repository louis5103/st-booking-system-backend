package com.springproject.stbookingsystem.repository;

import com.springproject.stbookingsystem.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    /**
     * 공연장명으로 조회
     */
    Optional<Venue> findByName(String name);

    /**
     * 공연장명 중복 확인
     */
    boolean existsByName(String name);

    /**
     * 위치로 공연장 검색
     */
    List<Venue> findByLocationContainingIgnoreCase(String location);

    /**
     * 공연장명으로 검색
     */
    List<Venue> findByNameContainingIgnoreCase(String name);

    /**
     * 활성 공연이 있는 공연장 조회
     */
    @Query("SELECT DISTINCT v FROM Venue v " +
           "JOIN v.performances p " +
           "WHERE p.performanceDate > CURRENT_TIMESTAMP " +
           "ORDER BY v.name")
    List<Venue> findVenuesWithActivePerformances();

    /**
     * 좌석 수 범위로 공연장 검색
     */
    List<Venue> findByTotalSeatsBetween(Integer minSeats, Integer maxSeats);

    /**
     * 모든 공연장을 생성일순으로 조회
     */
    List<Venue> findAllByOrderByCreatedAtDesc();

    /**
     * 모든 공연장을 이름순으로 조회
     */
    List<Venue> findAllByOrderByNameAsc();

    /**
     * 특정 지역의 공연장 개수 조회
     */
    @Query("SELECT COUNT(v) FROM Venue v WHERE v.location LIKE %:location%")
    Long countByLocationContaining(@Param("location") String location);

    /**
     * 공연장별 공연 수 통계
     */
    @Query("SELECT v.name, COUNT(p) FROM Venue v " +
           "LEFT JOIN v.performances p " +
           "GROUP BY v.id, v.name " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> getVenuePerformanceStatistics();
}
