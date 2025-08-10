package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.Performance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class PerformanceDTO {

    /**
     * 공연 등록/수정 요청 DTO
     */
    public static class PerformanceRequest {
        @NotBlank(message = "공연명은 필수입니다")
        private String title;

        @NotBlank(message = "공연장은 필수입니다")
        private String venue;

        @NotNull(message = "공연 일시는 필수입니다")
        private LocalDateTime performanceDate;

        @Positive(message = "가격은 0보다 커야 합니다")
        private Integer price;

        @Positive(message = "총 좌석 수는 0보다 커야 합니다")
        private Integer totalSeats;

        private String description;

        private String imageUrl;

        // 기본 생성자
        public PerformanceRequest() {}

        // 생성자
        public PerformanceRequest(String title, String venue, LocalDateTime performanceDate,
                                  Integer price, Integer totalSeats) {
            this.title = title;
            this.venue = venue;
            this.performanceDate = performanceDate;
            this.price = price;
            this.totalSeats = totalSeats;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public LocalDateTime getPerformanceDate() {
            return performanceDate;
        }

        public void setPerformanceDate(LocalDateTime performanceDate) {
            this.performanceDate = performanceDate;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(Integer totalSeats) {
            this.totalSeats = totalSeats;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    /**
     * 공연 응답 DTO
     */
    public static class PerformanceResponse {
        private Long id;
        private String title;
        private String venue;
        private LocalDateTime performanceDate;
        private Integer price;
        private Integer totalSeats;
        private Integer bookedSeats;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 기본 생성자
        public PerformanceResponse() {}

        // Entity로부터 DTO 생성
        public static PerformanceResponse from(Performance performance) {
            PerformanceResponse response = new PerformanceResponse();
            response.setId(performance.getId());
            response.setTitle(performance.getTitle());
            response.setVenue(performance.getVenue());
            response.setPerformanceDate(performance.getPerformanceDate());
            response.setPrice(performance.getPrice());
            response.setTotalSeats(performance.getTotalSeats());
            response.setBookedSeats(performance.getBookedSeats());
            response.setDescription(performance.getDescription());
            response.setImageUrl(performance.getImageUrl());
            response.setCreatedAt(performance.getCreatedAt());
            response.setUpdatedAt(performance.getUpdatedAt());
            return response;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
        }

        public LocalDateTime getPerformanceDate() {
            return performanceDate;
        }

        public void setPerformanceDate(LocalDateTime performanceDate) {
            this.performanceDate = performanceDate;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(Integer totalSeats) {
            this.totalSeats = totalSeats;
        }

        public Integer getBookedSeats() {
            return bookedSeats;
        }

        public void setBookedSeats(Integer bookedSeats) {
            this.bookedSeats = bookedSeats;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}