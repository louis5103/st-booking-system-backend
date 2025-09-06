package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.SeatLayout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class SeatLayoutDTO {

    /**
     * 좌석 배치 등록/수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatLayoutRequest {
        @NotNull(message = "공연장 ID는 필수입니다")
        private Long venueId;

        @NotNull(message = "행 번호는 필수입니다")
        @Positive(message = "행 번호는 양수여야 합니다")
        private Integer rowNumber;

        @NotNull(message = "좌석 번호는 필수입니다")
        @Positive(message = "좌석 번호는 양수여야 합니다")
        private Integer seatNumber;

        private String seatLabel; // 자동 생성되므로 선택사항

        @NotNull(message = "좌석 타입은 필수입니다")
        private SeatLayout.SeatType seatType;

        private Boolean isActive = true;
        private String description;
        private Integer xPosition;
        private Integer yPosition;
    }

    /**
     * 좌석 배치 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatLayoutResponse {
        private Long id;
        private Long venueId;
        private String venueName;
        private Integer rowNumber;
        private Integer seatNumber;
        private String seatLabel;
        private SeatLayout.SeatType seatType;
        private String seatTypeDescription;
        private Boolean isActive;
        private String description;
        private Integer xPosition;
        private Integer yPosition;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Entity로부터 DTO 생성
        public static SeatLayoutResponse from(SeatLayout seatLayout) {
            return SeatLayoutResponse.builder()
                    .id(seatLayout.getId())
                    .venueId(seatLayout.getVenue().getId())
                    .venueName(seatLayout.getVenue().getName())
                    .rowNumber(seatLayout.getRowNumber())
                    .seatNumber(seatLayout.getSeatNumber())
                    .seatLabel(seatLayout.getSeatLabel())
                    .seatType(seatLayout.getSeatType())
                    .seatTypeDescription(seatLayout.getSeatType().getDescription())
                    .isActive(seatLayout.getIsActive())
                    .description(seatLayout.getDescription())
                    .xPosition(seatLayout.getXPosition())
                    .yPosition(seatLayout.getYPosition())
                    .createdAt(seatLayout.getCreatedAt())
                    .updatedAt(seatLayout.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 좌석 배치 간단 정보 DTO (좌석 맵용)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatLayoutSimple {
        private Long id;
        private Integer rowNumber;
        private Integer seatNumber;
        private String seatLabel;
        private SeatLayout.SeatType seatType;
        private Boolean isActive;
        private Boolean isBookable;
        private Integer xPosition;
        private Integer yPosition;

        public static SeatLayoutSimple from(SeatLayout seatLayout) {
            return SeatLayoutSimple.builder()
                    .id(seatLayout.getId())
                    .rowNumber(seatLayout.getRowNumber())
                    .seatNumber(seatLayout.getSeatNumber())
                    .seatLabel(seatLayout.getSeatLabel())
                    .seatType(seatLayout.getSeatType())
                    .isActive(seatLayout.getIsActive())
                    .isBookable(seatLayout.isBookable())
                    .xPosition(seatLayout.getXPosition())
                    .yPosition(seatLayout.getYPosition())
                    .build();
        }
    }

    /**
     * 좌석 배치 일괄 생성 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatLayoutBulkRequest {
        @NotNull(message = "공연장 ID는 필수입니다")
        private Long venueId;

        @NotNull(message = "시작 행은 필수입니다")
        @Positive(message = "시작 행은 양수여야 합니다")
        private Integer startRow;

        @NotNull(message = "끝 행은 필수입니다")
        @Positive(message = "끝 행은 양수여야 합니다")
        private Integer endRow;

        @NotNull(message = "행당 좌석 수는 필수입니다")
        @Positive(message = "행당 좌석 수는 양수여야 합니다")
        private Integer seatsPerRow;

        private SeatLayout.SeatType seatType = SeatLayout.SeatType.REGULAR;
        private Boolean isActive = true;
        private List<SeatLayoutException> exceptions; // 특별 처리할 좌석들
    }

    /**
     * 좌석 배치 예외 처리 DTO (특정 좌석을 다르게 설정)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatLayoutException {
        private Integer rowNumber;
        private Integer seatNumber;
        private SeatLayout.SeatType seatType;
        private Boolean isActive;
        private String description;
    }

    /**
     * 좌석 배치 통계 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SeatLayoutStatistics {
        private Long venueId;
        private String venueName;
        private Integer totalSeats;
        private Integer activeSeats;
        private Integer bookableSeats;
        private Integer regularSeats;
        private Integer vipSeats;
        private Integer premiumSeats;
        private Integer wheelchairSeats;
        private Integer blockedSeats;
        private Integer aisleSpaces;
        private Integer stageAreas;

        public static SeatLayoutStatistics create(Long venueId, String venueName) {
            return SeatLayoutStatistics.builder()
                    .venueId(venueId)
                    .venueName(venueName)
                    .totalSeats(0)
                    .activeSeats(0)
                    .bookableSeats(0)
                    .regularSeats(0)
                    .vipSeats(0)
                    .premiumSeats(0)
                    .wheelchairSeats(0)
                    .blockedSeats(0)
                    .aisleSpaces(0)
                    .stageAreas(0)
                    .build();
        }
    }

    /**
     * 공연장 좌석 맵 전체 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VenueSeatMap {
        private Long venueId;
        private String venueName;
        private Integer totalRows;
        private Integer seatsPerRow;
        private List<List<SeatLayoutSimple>> seatMatrix; // 행별 좌석 배치
        private SeatLayoutStatistics statistics;

        public static VenueSeatMap from(Long venueId, String venueName, Integer totalRows, 
                                      Integer seatsPerRow, List<List<SeatLayoutSimple>> seatMatrix,
                                      SeatLayoutStatistics statistics) {
            return VenueSeatMap.builder()
                    .venueId(venueId)
                    .venueName(venueName)
                    .totalRows(totalRows)
                    .seatsPerRow(seatsPerRow)
                    .seatMatrix(seatMatrix)
                    .statistics(statistics)
                    .build();
        }
    }
}
