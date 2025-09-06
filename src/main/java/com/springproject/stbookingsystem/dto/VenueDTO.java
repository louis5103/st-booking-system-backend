package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.Venue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class VenueDTO {

    /**
     * 공연장 등록/수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueRequest {
        @NotBlank(message = "공연장명은 필수입니다")
        private String name;

        @NotBlank(message = "주소는 필수입니다")
        private String location;

        private String description;

        @NotNull(message = "전체 좌석 수는 필수입니다")
        @Positive(message = "전체 좌석 수는 양수여야 합니다")
        private Integer totalSeats;

        @NotNull(message = "행 수는 필수입니다")
        @Positive(message = "행 수는 양수여야 합니다")
        private Integer totalRows;

        @NotNull(message = "행당 좌석 수는 필수입니다")
        @Positive(message = "행당 좌석 수는 양수여야 합니다")
        private Integer seatsPerRow;

        private String facilities;
        private String contactInfo;
    }

    /**
     * 공연장 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueResponse {
        private Long id;
        private String name;
        private String location;
        private String description;
        private Integer totalSeats;
        private Integer totalRows;
        private Integer seatsPerRow;
        private String facilities;
        private String contactInfo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 추가 정보
        private Integer activePerformanceCount;
        private Integer totalPerformanceCount;
        private Boolean hasActivePerformances;

        // Entity로부터 DTO 생성
        public static VenueResponse from(Venue venue) {
            return VenueResponse.builder()
                    .id(venue.getId())
                    .name(venue.getName())
                    .location(venue.getLocation())
                    .description(venue.getDescription())
                    .totalSeats(venue.getTotalSeats())
                    .totalRows(venue.getTotalRows())
                    .seatsPerRow(venue.getSeatsPerRow())
                    .facilities(venue.getFacilities())
                    .contactInfo(venue.getContactInfo())
                    .createdAt(venue.getCreatedAt())
                    .updatedAt(venue.getUpdatedAt())
                    .hasActivePerformances(venue.hasActivePerformances())
                    .totalPerformanceCount(venue.getPerformances().size())
                    .build();
        }

        // 추가 정보와 함께 생성
        public static VenueResponse fromWithCounts(Venue venue, Integer activeCount) {
            VenueResponse response = from(venue);
            response.activePerformanceCount = activeCount;
            return response;
        }
    }

    /**
     * 공연장 간단 정보 DTO (목록용)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueSimple {
        private Long id;
        private String name;
        private String location;
        private Integer totalSeats;
        private Boolean hasActivePerformances;

        public static VenueSimple from(Venue venue) {
            return VenueSimple.builder()
                    .id(venue.getId())
                    .name(venue.getName())
                    .location(venue.getLocation())
                    .totalSeats(venue.getTotalSeats())
                    .hasActivePerformances(venue.hasActivePerformances())
                    .build();
        }
    }

    /**
     * 공연장 통계 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueStatistics {
        private Long venueId;
        private String venueName;
        private Integer totalSeats;
        private Integer totalPerformances;
        private Integer activePerformances;
        private Integer totalBookings;
        private Double averageBookingRate;
        private Long totalRevenue;

        public static VenueStatistics from(Venue venue, Integer totalPerformances, 
                                         Integer activePerformances, Integer totalBookings,
                                         Double averageBookingRate, Long totalRevenue) {
            return VenueStatistics.builder()
                    .venueId(venue.getId())
                    .venueName(venue.getName())
                    .totalSeats(venue.getTotalSeats())
                    .totalPerformances(totalPerformances)
                    .activePerformances(activePerformances)
                    .totalBookings(totalBookings)
                    .averageBookingRate(averageBookingRate)
                    .totalRevenue(totalRevenue)
                    .build();
        }
    }

    /**
     * 공연장 검색 조건 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueSearchRequest {
        private String name;
        private String location;
        private Integer minSeats;
        private Integer maxSeats;
        private Boolean hasActivePerformances;
        private String sortBy; // name, location, totalSeats, createdAt
        private String sortDirection; // asc, desc
    }

    /**
     * 공연장 좌석 구조 수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueSeatStructureRequest {
        @NotNull(message = "전체 좌석 수는 필수입니다")
        @Positive(message = "전체 좌석 수는 양수여야 합니다")
        private Integer totalSeats;

        @NotNull(message = "행 수는 필수입니다")
        @Positive(message = "행 수는 양수여야 합니다")
        private Integer totalRows;

        @NotNull(message = "행당 좌석 수는 필수입니다")
        @Positive(message = "행당 좌석 수는 양수여야 합니다")
        private Integer seatsPerRow;
    }
}
