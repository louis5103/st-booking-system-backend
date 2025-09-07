package com.springproject.stbookingsystem.dto;

import com.springproject.stbookingsystem.entity.SeatLayout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SeatLayoutDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private String id;
        private Integer x;
        private Integer y;
        private String type; // REGULAR, VIP, PREMIUM, WHEELCHAIR, BLOCKED
        private Integer section;
        private String label;
        private Integer price;
        private Boolean isActive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlexibleSeatInfo {
        private String id;
        private Integer x;
        private Integer y;
        private Integer sectionId;
        private String sectionName;
        private String sectionColor;
        private Integer rotation;
        private Integer price;
        private String seatType;
        private Boolean isActive;
        private String seatLabel;
        private Integer xPosition;
        private Integer yPosition;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlexibleLayoutRequest {
        private Long venueId;
        private List<FlexibleSeatInfo> seats;
        private StageInfo stage;
        private CanvasInfo canvasSize;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlexibleLayoutResponse {
        private Long venueId;
        private String venueName;
        private List<FlexibleSeatInfo> seats;
        private List<SectionInfo> sections;
        private StageInfo stage;
        private CanvasInfo canvasSize;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionInfo {
        private Integer id;
        private String name;
        private String color;
        private Integer seatCount;
        private Integer totalRevenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageInfo {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
        private Integer rotation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueLayoutResponse {
        private Long venueId;
        private String venueName;
        private List<SeatInfo> seats;
        private List<SectionInfo> sections;
        private StageInfo stage;
        private VenueStatistics statistics;
        private CanvasInfo canvas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueLayoutRequest {
        private List<SeatInfo> seats;
        private List<SectionInfo> sections;
        private StageInfo stage;
        private CanvasInfo canvas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CanvasInfo {
        private Integer width;
        private Integer height;
        private Integer gridSize;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VenueStatistics {
        private Integer totalSeats;
        private Integer activeSeats;
        private Integer totalRevenue;
        private Integer regularSeats;
        private Integer vipSeats;
        private Integer premiumSeats;
        private Integer wheelchairSeats;
        private Integer blockedSeats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateInfo {
        private String name;
        private String displayName;
        private String description;
        private Integer rows;
        private Integer cols;
        private Integer estimatedSeats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateConfig {
        private Integer rows;
        private Integer cols;
        private List<Integer> aisleColumns;
        private Boolean includePremiumSection;
        private Boolean includeVipSection;
    }

    // 엔티티 변환 메서드들
    public static SeatInfo fromEntity(SeatLayout entity) {
        return SeatInfo.builder()
                .id(entity.getId().toString())
                .x(entity.getXPosition())
                .y(entity.getYPosition())
                .type(entity.getSeatType().name())
                .section(entity.getSectionId())
                .label(entity.getSeatLabel())
                .price(entity.getPrice())
                .isActive(entity.getIsActive())
                .build();
    }

    public static FlexibleSeatInfo fromEntityFlexible(SeatLayout entity) {
        return FlexibleSeatInfo.builder()
                .id(entity.getId().toString())
                .x(entity.getXPosition())
                .y(entity.getYPosition())
                .xPosition(entity.getXPosition())
                .yPosition(entity.getYPosition())
                .sectionId(entity.getSectionId())
                .sectionName(entity.getSectionName())
                .sectionColor(entity.getSectionColor())
                .rotation(entity.getRotation())
                .price(entity.getPrice())
                .seatType(entity.getSeatType().name())
                .isActive(entity.getIsActive())
                .seatLabel(entity.getSeatLabel())
                .build();
    }

    public static SeatLayout toEntity(SeatInfo dto, com.springproject.stbookingsystem.entity.Venue venue) {
        return SeatLayout.builder()
                .venue(venue)
                .xPosition(dto.getX())
                .yPosition(dto.getY())
                .seatType(SeatLayout.SeatType.valueOf(dto.getType()))
                .sectionId(dto.getSection())
                .seatLabel(dto.getLabel())
                .price(dto.getPrice())
                .isActive(dto.getIsActive())
                // 기본값들
                .rowNumber(1)
                .seatNumber(1)
                .build();
    }

    public static SeatLayout toEntityFlexible(FlexibleSeatInfo dto, com.springproject.stbookingsystem.entity.Venue venue) {
        return SeatLayout.builder()
                .venue(venue)
                .xPosition(dto.getX() != null ? dto.getX() : dto.getXPosition())
                .yPosition(dto.getY() != null ? dto.getY() : dto.getYPosition())
                .seatType(SeatLayout.SeatType.valueOf(dto.getSeatType()))
                .sectionId(dto.getSectionId())
                .sectionName(dto.getSectionName())
                .sectionColor(dto.getSectionColor())
                .rotation(dto.getRotation())
                .seatLabel(dto.getSeatLabel())
                .price(dto.getPrice())
                .isActive(dto.getIsActive())
                // 기본값들
                .rowNumber(1)
                .seatNumber(1)
                .build();
    }
}