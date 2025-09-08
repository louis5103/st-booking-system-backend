package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.SeatLayout;
import com.springproject.stbookingsystem.entity.Venue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class SeatLayoutDTO {

    /**
     * 통합된 좌석 정보 - 격자 모드와 자유 배치 모드를 모두 지원
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnifiedSeatInfo {
        private String id;
        
        @NotNull
        @Min(0)
        private Integer x;
        
        @NotNull
        @Min(0)
        private Integer y;
        
        @NotBlank
        private String type; // REGULAR, VIP, PREMIUM, WHEELCHAIR, BLOCKED
        
        @NotNull
        @Min(1)
        private Integer section;
        
        @NotBlank
        private String label;
        
        @NotNull
        @Min(0)
        private Integer price;
        
        @Builder.Default
        private Boolean isActive = true;
        
        @Builder.Default
        private Integer rotation = 0;
        
        // 호환성을 위한 필드들
        private Integer xPosition;
        private Integer yPosition;
        private String seatType;
        private Integer sectionId;
        private String seatLabel;
    }

    /**
     * 섹션 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionInfo {
        @NotNull
        @Min(1)
        private Integer id;
        
        @NotBlank
        private String name;
        
        @NotBlank
        private String color;
        
        @Builder.Default
        private Integer seatCount = 0;
        
        @Builder.Default
        private Integer totalRevenue = 0;
    }

    /**
     * 무대 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageInfo {
        @NotNull
        @Min(0)
        private Integer x;
        
        @NotNull
        @Min(0)
        private Integer y;
        
        @NotNull
        @Min(50)
        @Max(500)
        private Integer width;
        
        @NotNull
        @Min(30)
        @Max(200)
        private Integer height;
        
        @Builder.Default
        private Integer rotation = 0;
    }

    /**
     * 캔버스 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CanvasInfo {
        @NotNull
        @Min(400)
        @Max(2000)
        private Integer width;
        
        @NotNull
        @Min(300)
        @Max(1500)
        private Integer height;
        
        @NotNull
        @Min(20)
        @Max(80)
        @Builder.Default
        private Integer gridSize = 40;
    }

    /**
     * 공연장 좌석 배치 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueLayoutResponse {
        @NotNull
        private Long venueId;
        
        @NotBlank
        private String venueName;
        
        @Valid
        private List<UnifiedSeatInfo> seats;
        
        @Valid
        private List<SectionInfo> sections;
        
        @Valid
        private StageInfo stage;
        
        @Valid
        private VenueStatistics statistics;
        
        @Valid
        private CanvasInfo canvas;
        
        private String editMode; // "grid" 또는 "free"
    }

    /**
     * 공연장 좌석 배치 요청
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueLayoutRequest {
        @Valid
        private List<UnifiedSeatInfo> seats;
        
        @Valid
        private List<SectionInfo> sections;
        
        @Valid
        private StageInfo stage;
        
        @Valid
        private CanvasInfo canvas;
        
        private String editMode; // "grid" 또는 "free"
    }

    /**
     * 통계 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueStatistics {
        @Builder.Default
        private Integer totalSeats = 0;
        
        @Builder.Default
        private Integer activeSeats = 0;
        
        @Builder.Default
        private Integer totalRevenue = 0;
        
        @Builder.Default
        private Integer regularSeats = 0;
        
        @Builder.Default
        private Integer vipSeats = 0;
        
        @Builder.Default
        private Integer premiumSeats = 0;
        
        @Builder.Default
        private Integer wheelchairSeats = 0;
        
        @Builder.Default
        private Integer blockedSeats = 0;
    }

    /**
     * 템플릿 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateInfo {
        @NotBlank
        private String name;
        
        @NotBlank
        private String displayName;
        
        private String description;
        
        @NotNull
        @Min(1)
        @Max(50)
        private Integer rows;
        
        @NotNull
        @Min(1)
        @Max(50)
        private Integer cols;
        
        private Integer estimatedSeats;
        
        private Boolean isPopular;
        
        private String category; // "theater", "concert", "stadium" 등
    }

    /**
     * 템플릿 설정
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateConfig {
        @Min(1)
        @Max(50)
        private Integer rows;
        
        @Min(1)
        @Max(50)
        private Integer cols;
        
        private List<Integer> aisleColumns;
        
        @Builder.Default
        private Boolean includePremiumSection = false;
        
        @Builder.Default
        private Boolean includeVipSection = false;
        
        @Builder.Default
        private Boolean includeWheelchairSection = false;
        
        private String seatPrefix; // 좌석 라벨 접두사
        
        @Builder.Default
        private String editMode = "grid"; // "grid" 또는 "free"
    }

    // =========================
    // 엔티티 변환 메서드들
    // =========================

    /**
     * SeatLayout 엔티티를 UnifiedSeatInfo DTO로 변환
     */
    public static UnifiedSeatInfo fromEntity(SeatLayout entity) {
        return UnifiedSeatInfo.builder()
                .id(entity.getId().toString())
                .x(entity.getXPosition())
                .y(entity.getYPosition())
                .type(entity.getSeatType().name())
                .section(entity.getSectionId())
                .label(entity.getSeatLabel())
                .price(entity.getPrice())
                .isActive(entity.getIsActive())
                .rotation(entity.getRotation() != null ? entity.getRotation() : 0)
                // 호환성 필드들
                .xPosition(entity.getXPosition())
                .yPosition(entity.getYPosition())
                .seatType(entity.getSeatType().name())
                .sectionId(entity.getSectionId())
                .seatLabel(entity.getSeatLabel())
                .build();
    }

    /**
     * UnifiedSeatInfo DTO를 SeatLayout 엔티티로 변환
     */
    public static SeatLayout toEntity(UnifiedSeatInfo dto, Venue venue) {
        // 좌표는 x, y를 우선하고 없으면 xPosition, yPosition 사용
        Integer xPos = dto.getX() != null ? dto.getX() : dto.getXPosition();
        Integer yPos = dto.getY() != null ? dto.getY() : dto.getYPosition();
        
        // 타입은 type을 우선하고 없으면 seatType 사용
        String seatType = dto.getType() != null ? dto.getType() : dto.getSeatType();
        
        // 섹션은 section을 우선하고 없으면 sectionId 사용
        Integer sectionId = dto.getSection() != null ? dto.getSection() : dto.getSectionId();
        
        // 라벨은 label을 우선하고 없으면 seatLabel 사용
        String label = dto.getLabel() != null ? dto.getLabel() : dto.getSeatLabel();
        
        // 좌표를 그리드 기반 행/열로 변환 (gridSize = 40 기준)
        Integer gridSize = 40;
        Integer rowNumber = yPos != null ? (yPos / gridSize) + 1 : 1;
        Integer seatNumber = xPos != null ? (xPos / gridSize) + 1 : 1;
        
        // 중복을 방지하기 위해 좌표값을 추가로 고려
        // 같은 그리드 셀 내에서도 유니크하게 만들기
        if (xPos != null && yPos != null) {
            int offsetX = xPos % gridSize;
            int offsetY = yPos % gridSize;
            // 오프셋을 이용해 동일 그리드 내에서 구분
            seatNumber = seatNumber * 100 + offsetX;
            rowNumber = rowNumber * 100 + offsetY;
        }
        
        return SeatLayout.builder()
                .venue(venue)
                .xPosition(xPos)
                .yPosition(yPos)
                .seatType(SeatLayout.SeatType.valueOf(seatType))
                .sectionId(sectionId)
                .seatLabel(label)
                .price(dto.getPrice())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .rotation(dto.getRotation() != null ? dto.getRotation() : 0)
                // 계산된 행/열 번호
                .rowNumber(rowNumber)
                .seatNumber(seatNumber)
                .build();
    }

    /**
     * 기본 무대 정보 생성
     */
    public static StageInfo createDefaultStage(Integer canvasWidth, Integer canvasHeight) {
        return StageInfo.builder()
                .x(canvasWidth != null ? (canvasWidth / 2) - 100 : 200)
                .y(50)
                .width(200)
                .height(60)
                .rotation(0)
                .build();
    }

    /**
     * 기본 캔버스 정보 생성
     */
    public static CanvasInfo createDefaultCanvas() {
        return CanvasInfo.builder()
                .width(800)
                .height(600)
                .gridSize(40)
                .build();
    }

    /**
     * 섹션별 통계 계산
     */
    public static SectionInfo calculateSectionStats(Integer sectionId, String sectionName, 
                                                  String sectionColor, List<UnifiedSeatInfo> seats) {
        List<UnifiedSeatInfo> sectionSeats = seats.stream()
                .filter(seat -> sectionId.equals(seat.getSection()))
                .toList();
        
        int totalRevenue = sectionSeats.stream()
                .mapToInt(seat -> seat.getPrice() != null ? seat.getPrice() : 0)
                .sum();
        
        return SectionInfo.builder()
                .id(sectionId)
                .name(sectionName)
                .color(sectionColor)
                .seatCount(sectionSeats.size())
                .totalRevenue(totalRevenue)
                .build();
    }

    /**
     * 전체 통계 계산
     */
    public static VenueStatistics calculateStatistics(List<UnifiedSeatInfo> seats) {
        int totalSeats = seats.size();
        int activeSeats = (int) seats.stream().filter(seat -> seat.getIsActive() != null && seat.getIsActive()).count();
        int totalRevenue = seats.stream().mapToInt(seat -> seat.getPrice() != null ? seat.getPrice() : 0).sum();

        // 좌석 타입별 카운트
        long regularSeats = seats.stream().filter(seat -> "REGULAR".equals(seat.getType())).count();
        long vipSeats = seats.stream().filter(seat -> "VIP".equals(seat.getType())).count();
        long premiumSeats = seats.stream().filter(seat -> "PREMIUM".equals(seat.getType())).count();
        long wheelchairSeats = seats.stream().filter(seat -> "WHEELCHAIR".equals(seat.getType())).count();
        long blockedSeats = seats.stream().filter(seat -> "BLOCKED".equals(seat.getType())).count();

        return VenueStatistics.builder()
                .totalSeats(totalSeats)
                .activeSeats(activeSeats)
                .totalRevenue(totalRevenue)
                .regularSeats((int) regularSeats)
                .vipSeats((int) vipSeats)
                .premiumSeats((int) premiumSeats)
                .wheelchairSeats((int) wheelchairSeats)
                .blockedSeats((int) blockedSeats)
                .build();
    }

    // =========================
    // 레거시 호환성 메서드들
    // =========================

    /**
     * 레거시 SeatInfo와의 호환성을 위한 변환
     */
    @Deprecated
    public static class SeatInfo extends UnifiedSeatInfo {
        // 레거시 호환성을 위해 유지
    }

    /**
     * 레거시 FlexibleSeatInfo와의 호환성을 위한 변환
     */
    @Deprecated
    public static class FlexibleSeatInfo extends UnifiedSeatInfo {
        // 레거시 호환성을 위해 유지
    }
}