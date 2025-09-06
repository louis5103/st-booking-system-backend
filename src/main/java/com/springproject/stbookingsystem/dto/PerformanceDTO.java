package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.Performance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PerformanceDTO {

    /**
     * 공연 등록/수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceRequest {
        @NotBlank(message = "공연명은 필수입니다")
        private String title;

        @NotNull(message = "공연장 ID는 필수입니다")
        private Long venueId;

        private String venueName; // 응답용 (선택사항)

        @NotNull(message = "공연 일시는 필수입니다")
        private LocalDateTime performanceDate;

        @Positive(message = "가격은 0보다 커야 합니다")
        private Integer price;

        @Positive(message = "총 좌석 수는 0보다 커야 합니다")
        private Integer totalSeats;

        private String description;

        private String imageUrl;
    }

    /**
     * 공연 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceResponse {
        private Long id;
        private String title;
        private Long venueId;
        private String venueName;
        private LocalDateTime performanceDate;
        private Integer price;
        private Integer totalSeats;
        private Integer bookedSeats;
        private String description;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Entity로부터 DTO 생성
        public static PerformanceResponse from(Performance performance) {
            return PerformanceResponse.builder()
                    .id(performance.getId())
                    .title(performance.getTitle())
                    .venueId(performance.getVenue() != null ? performance.getVenue().getId() : null)
                    .venueName(performance.getVenueName())
                    .performanceDate(performance.getPerformanceDate())
                    .price(performance.getPrice())
                    .totalSeats(performance.getTotalSeats())
                    .bookedSeats(performance.getBookedSeats())
                    .description(performance.getDescription())
                    .imageUrl(performance.getImageUrl())
                    .createdAt(performance.getCreatedAt())
                    .updatedAt(performance.getUpdatedAt())
                    .build();
        }
    }
}