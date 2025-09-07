package com.springproject.stbookingsystem.service;

import com.springproject.stbookingsystem.dto.SeatLayoutDTO;
import com.springproject.stbookingsystem.entity.SeatLayout;
import com.springproject.stbookingsystem.entity.Venue;
import com.springproject.stbookingsystem.repository.SeatLayoutRepository;
import com.springproject.stbookingsystem.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatLayoutService {

    private final SeatLayoutRepository seatLayoutRepository;
    private final VenueRepository venueRepository;

    // 템플릿 정의
    private static final Map<String, SeatLayoutDTO.TemplateInfo> TEMPLATES = Map.of(
        "small_theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("small_theater")
            .displayName("소형 극장")
            .description("10행 8열의 소형 극장 배치")
            .rows(10).cols(8).estimatedSeats(70)
            .build(),
        "medium_theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("medium_theater")
            .displayName("중형 극장")
            .description("15행 12열의 중형 극장 배치")
            .rows(15).cols(12).estimatedSeats(160)
            .build(),
        "large_theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("large_theater")
            .displayName("대형 극장")
            .description("20행 16열의 대형 극장 배치")
            .rows(20).cols(16).estimatedSeats(280)
            .build(),
        "concert_hall", SeatLayoutDTO.TemplateInfo.builder()
            .name("concert_hall")
            .displayName("콘서트홀")
            .description("25행 20열의 콘서트홀 배치")
            .rows(25).cols(20).estimatedSeats(450)
            .build()
    );

    /**
     * 공연장 좌석 배치 조회
     */
    public SeatLayoutDTO.VenueLayoutResponse getVenueLayout(Long venueId) {
        log.info("공연장 좌석 배치 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        
        // 좌석 정보 변환
        List<SeatLayoutDTO.SeatInfo> seats = seatLayouts.stream()
                .map(SeatLayoutDTO::fromEntity)
                .collect(Collectors.toList());

        // 섹션 정보 생성
        List<SeatLayoutDTO.SectionInfo> sections = generateSectionInfo(seatLayouts);

        // 통계 생성
        SeatLayoutDTO.VenueStatistics statistics = generateStatistics(seatLayouts);

        // 기본 무대 정보
        SeatLayoutDTO.StageInfo stage = SeatLayoutDTO.StageInfo.builder()
                .x(200).y(50).width(200).height(60).rotation(0)
                .build();

        // 기본 캔버스 정보
        SeatLayoutDTO.CanvasInfo canvas = SeatLayoutDTO.CanvasInfo.builder()
                .width(800).height(600).gridSize(40)
                .build();

        return SeatLayoutDTO.VenueLayoutResponse.builder()
                .venueId(venueId)
                .venueName(venue.getName())
                .seats(seats)
                .sections(sections)
                .stage(stage)
                .statistics(statistics)
                .canvas(canvas)
                .build();
    }

    /**
     * 유연한 좌석 배치 조회
     */
    public SeatLayoutDTO.FlexibleLayoutResponse getFlexibleSeatMap(Long venueId) {
        log.info("유연한 좌석 배치 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        
        // 좌석 정보 변환 (유연한 배치용)
        List<SeatLayoutDTO.FlexibleSeatInfo> seats = seatLayouts.stream()
                .map(SeatLayoutDTO::fromEntityFlexible)
                .collect(Collectors.toList());

        // 섹션 정보 생성
        List<SeatLayoutDTO.SectionInfo> sections = generateSectionInfo(seatLayouts);

        // 기본 무대 정보
        SeatLayoutDTO.StageInfo stage = SeatLayoutDTO.StageInfo.builder()
                .x(300).y(50).width(200).height(60).rotation(0)
                .build();

        // 기본 캔버스 정보
        SeatLayoutDTO.CanvasInfo canvasSize = SeatLayoutDTO.CanvasInfo.builder()
                .width(800).height(600).gridSize(20)
                .build();

        return SeatLayoutDTO.FlexibleLayoutResponse.builder()
                .venueId(venueId)
                .venueName(venue.getName())
                .seats(seats)
                .sections(sections)
                .stage(stage)
                .canvasSize(canvasSize)
                .build();
    }

    /**
     * 유연한 좌석 배치 저장
     */
    @Transactional
    public SeatLayoutDTO.FlexibleLayoutResponse updateFlexibleLayout(SeatLayoutDTO.FlexibleLayoutRequest request) {
        log.info("유연한 좌석 배치 저장: venueId = {}, seatCount = {}", 
                request.getVenueId(), 
                request.getSeats() != null ? request.getSeats().size() : 0);

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + request.getVenueId()));

        // 기존 좌석 삭제
        seatLayoutRepository.deleteByVenue(venue);

        // 새 좌석 생성
        List<SeatLayout> newSeats = new ArrayList<>();
        if (request.getSeats() != null) {
            for (SeatLayoutDTO.FlexibleSeatInfo seatInfo : request.getSeats()) {
                SeatLayout seat = SeatLayoutDTO.toEntityFlexible(seatInfo, venue);
                newSeats.add(seat);
            }
        }

        // 저장
        List<SeatLayout> savedSeats = seatLayoutRepository.saveAll(newSeats);
        log.info("유연한 좌석 배치 저장 완료: {} 개 좌석", savedSeats.size());

        return getFlexibleSeatMap(request.getVenueId());
    }

    /**
     * 좌석 배치 저장
     */
    @Transactional
    public SeatLayoutDTO.VenueLayoutResponse saveVenueLayout(Long venueId, SeatLayoutDTO.VenueLayoutRequest request) {
        log.info("좌석 배치 저장: venueId = {}, seatCount = {}", venueId, 
                request.getSeats() != null ? request.getSeats().size() : 0);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        // 기존 좌석 삭제
        seatLayoutRepository.deleteByVenue(venue);

        // 새 좌석 생성
        List<SeatLayout> newSeats = new ArrayList<>();
        if (request.getSeats() != null) {
            for (SeatLayoutDTO.SeatInfo seatInfo : request.getSeats()) {
                SeatLayout seat = SeatLayoutDTO.toEntity(seatInfo, venue);
                newSeats.add(seat);
            }
        }

        // 저장
        List<SeatLayout> savedSeats = seatLayoutRepository.saveAll(newSeats);
        log.info("좌석 배치 저장 완료: {} 개 좌석", savedSeats.size());

        return getVenueLayout(venueId);
    }

    /**
     * 템플릿 적용
     */
    @Transactional
    public SeatLayoutDTO.VenueLayoutResponse applyTemplate(Long venueId, String templateName, SeatLayoutDTO.TemplateConfig config) {
        log.info("템플릿 적용: venueId = {}, template = {}", venueId, templateName);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        SeatLayoutDTO.TemplateInfo template = TEMPLATES.get(templateName);
        if (template == null) {
            throw new RuntimeException("존재하지 않는 템플릿입니다: " + templateName);
        }

        // 기존 좌석 삭제
        seatLayoutRepository.deleteByVenue(venue);

        // 템플릿에 따른 좌석 생성
        List<SeatLayout> templateSeats = generateTemplateSeats(venue, template, config);
        
        // 저장
        List<SeatLayout> savedSeats = seatLayoutRepository.saveAll(templateSeats);
        log.info("템플릿 적용 완료: {} 개 좌석 생성", savedSeats.size());

        return getVenueLayout(venueId);
    }

    /**
     * 좌석 배치 초기화
     */
    @Transactional
    public void clearVenueLayout(Long venueId) {
        log.info("좌석 배치 초기화: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        seatLayoutRepository.deleteByVenue(venue);
        log.info("좌석 배치 초기화 완료");
    }

    /**
     * 사용 가능한 템플릿 목록 조회
     */
    public List<SeatLayoutDTO.TemplateInfo> getAvailableTemplates() {
        return new ArrayList<>(TEMPLATES.values());
    }

    /**
     * 공연장 통계 조회
     */
    public SeatLayoutDTO.VenueStatistics getVenueStatistics(Long venueId) {
        log.info("공연장 통계 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        return generateStatistics(seatLayouts);
    }

    // 프라이빗 메서드들

    /**
     * 템플릿 좌석 생성
     */
    private List<SeatLayout> generateTemplateSeats(Venue venue, SeatLayoutDTO.TemplateInfo template, SeatLayoutDTO.TemplateConfig config) {
        List<SeatLayout> seats = new ArrayList<>();
        
        int rows = config != null && config.getRows() != null ? config.getRows() : template.getRows();
        int cols = config != null && config.getCols() != null ? config.getCols() : template.getCols();
        Set<Integer> aisleColumns = config != null && config.getAisleColumns() != null ? 
                new HashSet<>(config.getAisleColumns()) : Set.of(cols / 2);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // 통로 건너뛰기
                if (aisleColumns.contains(col)) continue;

                // 섹션 결정
                int section = determineSectionByPosition(row, rows, config);
                
                // 좌석 타입 결정
                SeatLayout.SeatType seatType = determineSeatTypeByPosition(row, rows, config);
                
                // 가격 결정
                int price = determinePriceByType(seatType);
                
                // 좌석 라벨 생성
                String label = String.valueOf((char)('A' + row)) + (col + 1);

                SeatLayout seat = SeatLayout.builder()
                        .venue(venue)
                        .xPosition(col + 2) // 왼쪽 여백
                        .yPosition(row + 4) // 무대 아래 여백
                        .seatType(seatType)
                        .sectionId(section)
                        .seatLabel(label)
                        .price(price)
                        .isActive(true)
                        .rowNumber(row + 1)
                        .seatNumber(col + 1)
                        .build();

                seats.add(seat);
            }
        }

        return seats;
    }

    /**
     * 위치에 따른 섹션 결정
     */
    private int determineSectionByPosition(int row, int totalRows, SeatLayoutDTO.TemplateConfig config) {
        if (row < totalRows * 0.3) {
            return 1; // 앞쪽 - 프리미엄
        } else if (row < totalRows * 0.7) {
            return 2; // 중간 - 일반
        } else {
            return 3; // 뒤쪽 - 이코노미
        }
    }

    /**
     * 위치에 따른 좌석 타입 결정
     */
    private SeatLayout.SeatType determineSeatTypeByPosition(int row, int totalRows, SeatLayoutDTO.TemplateConfig config) {
        if (config != null && config.getIncludeVipSection() != null && config.getIncludeVipSection() && row < 2) {
            return SeatLayout.SeatType.VIP;
        } else if (config != null && config.getIncludePremiumSection() != null && config.getIncludePremiumSection() && row < totalRows * 0.3) {
            return SeatLayout.SeatType.PREMIUM;
        } else {
            return SeatLayout.SeatType.REGULAR;
        }
    }

    /**
     * 좌석 타입에 따른 가격 결정
     */
    private int determinePriceByType(SeatLayout.SeatType seatType) {
        return switch (seatType) {
            case VIP -> 100000;
            case PREMIUM -> 75000;
            case REGULAR -> 50000;
            case WHEELCHAIR -> 50000;
            case BLOCKED -> 0;
            default -> 50000;
        };
    }

    /**
     * 섹션 정보 생성
     */
    private List<SeatLayoutDTO.SectionInfo> generateSectionInfo(List<SeatLayout> seatLayouts) {
        Map<Integer, List<SeatLayout>> sectionMap = seatLayouts.stream()
                .filter(seat -> seat.getSectionId() != null)
                .collect(Collectors.groupingBy(SeatLayout::getSectionId));

        List<SeatLayoutDTO.SectionInfo> sections = new ArrayList<>();
        
        // 기본 섹션 색상
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57"};
        
        for (Map.Entry<Integer, List<SeatLayout>> entry : sectionMap.entrySet()) {
            Integer sectionId = entry.getKey();
            List<SeatLayout> sectionSeats = entry.getValue();
            
            String sectionName = sectionId + "구역";
            String sectionColor = colors[(sectionId - 1) % colors.length];
            
            int totalRevenue = sectionSeats.stream()
                    .mapToInt(seat -> seat.getPrice() != null ? seat.getPrice() : 0)
                    .sum();

            sections.add(SeatLayoutDTO.SectionInfo.builder()
                    .id(sectionId)
                    .name(sectionName)
                    .color(sectionColor)
                    .seatCount(sectionSeats.size())
                    .totalRevenue(totalRevenue)
                    .build());
        }

        return sections;
    }

    /**
     * 통계 생성
     */
    private SeatLayoutDTO.VenueStatistics generateStatistics(List<SeatLayout> seatLayouts) {
        int totalSeats = seatLayouts.size();
        int activeSeats = (int) seatLayouts.stream().filter(SeatLayout::getIsActive).count();
        int totalRevenue = seatLayouts.stream().mapToInt(seat -> seat.getPrice() != null ? seat.getPrice() : 0).sum();

        Map<SeatLayout.SeatType, Long> typeCounts = seatLayouts.stream()
                .collect(Collectors.groupingBy(SeatLayout::getSeatType, Collectors.counting()));

        return SeatLayoutDTO.VenueStatistics.builder()
                .totalSeats(totalSeats)
                .activeSeats(activeSeats)
                .totalRevenue(totalRevenue)
                .regularSeats(typeCounts.getOrDefault(SeatLayout.SeatType.REGULAR, 0L).intValue())
                .vipSeats(typeCounts.getOrDefault(SeatLayout.SeatType.VIP, 0L).intValue())
                .premiumSeats(typeCounts.getOrDefault(SeatLayout.SeatType.PREMIUM, 0L).intValue())
                .wheelchairSeats(typeCounts.getOrDefault(SeatLayout.SeatType.WHEELCHAIR, 0L).intValue())
                .blockedSeats(typeCounts.getOrDefault(SeatLayout.SeatType.BLOCKED, 0L).intValue())
                .build();
    }
}