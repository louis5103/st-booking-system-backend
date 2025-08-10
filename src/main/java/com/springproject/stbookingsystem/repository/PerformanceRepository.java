package com.springproject.stbookingsystem.repository;


import com.springproject.stbookingsystem.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    /**
     * 제목으로 공연 검색 (부분 일치)
     */
    List<Performance> findByTitleContainingIgnoreCase(String title);

    /**
     * 공연장으로 공연 검색 (부분 일치)
     */
    List<Performance> findByVenueContainingIgnoreCase(String venue);

    /**
     * 제목 또는 공연장으로 공연 검색
     */
    @Query("SELECT p FROM Performance p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.venue) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Performance> findByTitleOrVenueContaining(@Param("keyword") String keyword);

    /**
     * 공연 일시 범위로 공연 찾기
     */
    List<Performance> findByPerformanceDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 미래 공연만 조회 (공연일이 현재보다 이후)
     */
    List<Performance> findByPerformanceDateAfterOrderByPerformanceDateAsc(LocalDateTime now);

    /**
     * 가격 범위로 공연 찾기
     */
    List<Performance> findByPriceBetween(Integer minPrice, Integer maxPrice);

    /**
     * 공연 일시순으로 정렬된 전체 공연 목록
     */
    List<Performance> findAllByOrderByPerformanceDateAsc();

    /**
     * 공연 제목순으로 정렬된 전체 공연 목록
     */
    List<Performance> findAllByOrderByTitleAsc();

    /**
     * 가격순으로 정렬된 전체 공연 목록
     */
    List<Performance> findAllByOrderByPriceAsc();
}