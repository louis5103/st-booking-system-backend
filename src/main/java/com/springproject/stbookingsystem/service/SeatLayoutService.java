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

    // 섹션 기본 색상
    private static final String[] SECTION_COLORS = {
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57", 
        "#FF8A80", "#82B1FF", "#B39DDB", "#A5D6A7", "#FFCC80"
    };

    // 템플릿 정의 (개선된 버전)
    private static final Map<String, SeatLayoutDTO.TemplateInfo> TEMPLATES;
    
    static {
        TEMPLATES = new HashMap<>();
        TEMPLATES.put("small_theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("small_theater")
            .displayName("소형 극장")
            .description("8행 6열의 아늑한 소형 극장 배치")
            .rows(8).cols(6).estimatedSeats(42)
            .category("theater")
            .isPopular(true)
            .build());
        TEMPLATES.put("medium_theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("medium_theater")
            .displayName("중형 극장")
            .description("12행 8열의 표준 중형 극장 배치")
            .rows(12).cols(8).estimatedSeats(84)
            .category("theater")
            .isPopular(true)
            .build());
        TEMPLATES.put("large_theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("large_theater")
            .displayName("대형 극장")
            .description("16행 10열의 대형 극장 배치")
            .rows(16).cols(10).estimatedSeats(140)
            .category("theater")
            .isPopular(false)
            .build());
        TEMPLATES.put("concert_hall", SeatLayoutDTO.TemplateInfo.builder()
            .name("concert_hall")
            .displayName("콘서트홀")
            .description("20행 12열의 대형 콘서트홀 배치")
            .rows(20).cols(12).estimatedSeats(210)
            .category("concert")
            .isPopular(true)
            .build());
        TEMPLATES.put("theater", SeatLayoutDTO.TemplateInfo.builder()
            .name("theater")
            .displayName("표준 극장")
            .description("20행 30열의 표준 극장 배치")
            .rows(20).cols(30).estimatedSeats(500)
            .category("theater")
            .isPopular(true)
            .build());
        TEMPLATES.put("classroom", SeatLayoutDTO.TemplateInfo.builder()
            .name("classroom")
            .displayName("강의실")
            .description("10행 20열의 교육용 배치")
            .rows(10).cols(20).estimatedSeats(180)
            .category("education")
            .isPopular(false)
            .build());
        TEMPLATES.put("stadium", SeatLayoutDTO.TemplateInfo.builder()
            .name("stadium")
            .displayName("스타디움")
            .description("50행 60열의 대규모 경기장")
            .rows(50).cols(60).estimatedSeats(2500)
            .category("sports")
            .isPopular(false)
            .build());
    }

    /**
     * 공연장 좌석 배치 조회 (통합 버전)
     */
    public SeatLayoutDTO.VenueLayoutResponse getVenueLayout(Long venueId) {
        log.info("공연장 좌석 배치 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        
        // 좌석 정보 변환 (통합된 DTO 사용)
        List<SeatLayoutDTO.UnifiedSeatInfo> seats = seatLayouts.stream()
                .map(SeatLayoutDTO::fromEntity)
                .collect(Collectors.toList());

        // 섹션 정보 생성
        List<SeatLayoutDTO.SectionInfo> sections = generateSectionInfo(seatLayouts);

        // 통계 생성
        SeatLayoutDTO.VenueStatistics statistics = SeatLayoutDTO.calculateStatistics(seats);

        // 무대 정보 (기본값 또는 저장된 값)
        SeatLayoutDTO.StageInfo stage = SeatLayoutDTO.createDefaultStage(800, 600);

        // 캔버스 정보 (기본값 또는 저장된 값)
        SeatLayoutDTO.CanvasInfo canvas = SeatLayoutDTO.createDefaultCanvas();

        return SeatLayoutDTO.VenueLayoutResponse.builder()
                .venueId(venueId)
                .venueName(venue.getName())
                .seats(seats)
                .sections(sections)
                .stage(stage)
                .statistics(statistics)
                .canvas(canvas)
                .editMode("grid") // 기본값
                .build();
    }

    /**
     * 좌석 배치 저장 (통합 버전)
     */
    @Transactional
    public SeatLayoutDTO.VenueLayoutResponse saveVenueLayout(Long venueId, SeatLayoutDTO.VenueLayoutRequest request) {
        log.info("좌석 배치 저장: venueId = {}, seatCount = {}", venueId, 
                request.getSeats() != null ? request.getSeats().size() : 0);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        // 1. 먼저 해당 venue의 seatLayout을 참조하는 seats를 찾아서 seatLayout 참조를 null로 설정
        List<SeatLayout> existingSeatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        for (SeatLayout seatLayout : existingSeatLayouts) {
            // seats 테이블에서 해당 seatLayout을 참조하는 레코드들의 seat_layout_id를 null로 설정
            seatLayoutRepository.clearSeatLayoutReferences(seatLayout.getId());
        }
        
        // 2. 이제 외래키 제약조건 없이 기존 좌석 배치 삭제 가능
        seatLayoutRepository.deleteByVenue(venue);

        // 새 좌석 생성
        List<SeatLayout> newSeats = new ArrayList<>();
        if (request.getSeats() != null) {
            for (SeatLayoutDTO.UnifiedSeatInfo seatInfo : request.getSeats()) {
                SeatLayout seat = SeatLayoutDTO.toEntity(seatInfo, venue);
                // 섹션 정보 추가 설정
                if (request.getSections() != null) {
                    SeatLayoutDTO.SectionInfo section = request.getSections().stream()
                            .filter(s -> s.getId().equals(seatInfo.getSection()))
                            .findFirst()
                            .orElse(null);
                    if (section != null) {
                        seat.setSectionName(section.getName());
                        seat.setSectionColor(section.getColor());
                    }
                }
                newSeats.add(seat);
            }
        }

        // 저장
        List<SeatLayout> savedSeats = seatLayoutRepository.saveAll(newSeats);
        log.info("좌석 배치 저장 완료: {} 개 좌석", savedSeats.size());

        return getVenueLayout(venueId);
    }

    /**
     * 템플릿 적용 (개선 버전)
     */
    @Transactional
    public SeatLayoutDTO.VenueLayoutResponse applyTemplate(Long venueId, String templateName, SeatLayoutDTO.TemplateConfig config) {
        log.info("템플릿 적용: venueId = {}, template = {}, config = {}", venueId, templateName, config);

        try {
            // 1. Venue 존재 확인
            Venue venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> {
                        log.error("공연장을 찾을 수 없습니다: venueId = {}", venueId);
                        return new RuntimeException("공연장을 찾을 수 없습니다: " + venueId);
                    });
            log.info("공연장 확인 완료: {}", venue.getName());

            // 2. 템플릿 존재 확인
            SeatLayoutDTO.TemplateInfo template = TEMPLATES.get(templateName);
            if (template == null) {
                log.error("존재하지 않는 템플릿입니다: {}, 사용 가능한 템플릿: {}", templateName, TEMPLATES.keySet());
                throw new RuntimeException("존재하지 않는 템플릿입니다: " + templateName);
            }
            log.info("템플릿 확인 완료: {}", template.getDisplayName());

            // 3. 기존 좌석 배치 삭제
            log.info("기존 좌석 배치 삭제 시작");
            List<SeatLayout> existingSeatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
            log.info("기존 좌석 배치 개수: {}", existingSeatLayouts.size());
            
            for (SeatLayout seatLayout : existingSeatLayouts) {
                try {
                    seatLayoutRepository.clearSeatLayoutReferences(seatLayout.getId());
                } catch (Exception e) {
                    log.warn("좌석 참조 제거 오류: seatLayoutId = {}, error = {}", seatLayout.getId(), e.getMessage());
                }
            }
            
            try {
                seatLayoutRepository.deleteByVenue(venue);
                log.info("기존 좌석 배치 삭제 완료");
            } catch (Exception e) {
                log.error("기존 좌석 배치 삭제 실패", e);
                throw new RuntimeException("기존 좌석 배치 삭제 실패: " + e.getMessage());
            }

            // 4. 템플릿에 따른 좌석 생성
            log.info("템플릿 좌석 생성 시작");
            List<SeatLayout> templateSeats = generateTemplateSeats(venue, template, config);
            log.info("생성될 좌석 수: {}", templateSeats.size());
        
            // 5. 저장
            try {
                List<SeatLayout> savedSeats = seatLayoutRepository.saveAll(templateSeats);
                log.info("템플릿 적용 완료: {} 개 좌석 생성", savedSeats.size());
            } catch (Exception e) {
                log.error("템플릿 좌석 저장 실패", e);
                throw new RuntimeException("템플릿 좌석 저장 실패: " + e.getMessage());
            }

            return getVenueLayout(venueId);
            
        } catch (Exception e) {
            log.error("템플릿 적용 중 오류 발생: venueId = {}, template = {}", venueId, templateName, e);
            throw e; // 원본 예외를 다시 던지기
        }
    }

    /**
     * 좌석 배치 초기화
     */
    @Transactional
    public void clearVenueLayout(Long venueId) {
        log.info("좌석 배치 초기화: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        // 1. 먼저 해당 venue의 seatLayout을 참조하는 seats를 찾아서 seatLayout 참조를 null로 설정
        List<SeatLayout> existingSeatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        for (SeatLayout seatLayout : existingSeatLayouts) {
            seatLayoutRepository.clearSeatLayoutReferences(seatLayout.getId());
        }
        
        // 2. 기존 좌석 배치 삭제
        seatLayoutRepository.deleteByVenue(venue);
        log.info("좌석 배치 초기화 완료");
    }

    /**
     * 사용 가능한 템플릿 목록 조회
     */
    public List<SeatLayoutDTO.TemplateInfo> getAvailableTemplates() {
        return new ArrayList<>(TEMPLATES.values())
                .stream()
                .sorted((t1, t2) -> {
                    // 인기 템플릿을 먼저 정렬
                    if (t1.getIsPopular() && !t2.getIsPopular()) return -1;
                    if (!t1.getIsPopular() && t2.getIsPopular()) return 1;
                    // 그 다음 크기순
                    return Integer.compare(t1.getEstimatedSeats(), t2.getEstimatedSeats());
                })
                .collect(Collectors.toList());
    }

    /**
     * 공연장 통계 조회
     */
    public SeatLayoutDTO.VenueStatistics getVenueStatistics(Long venueId) {
        log.info("공연장 통계 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByIdAsc(venue);
        List<SeatLayoutDTO.UnifiedSeatInfo> seats = seatLayouts.stream()
                .map(SeatLayoutDTO::fromEntity)
                .collect(Collectors.toList());
        
        return SeatLayoutDTO.calculateStatistics(seats);
    }

    // =========================
    // 프라이빗 헬퍼 메서드들
    // =========================

    /**
     * 템플릿 좌석 생성 (개선된 버전)
     */
    private List<SeatLayout> generateTemplateSeats(Venue venue, SeatLayoutDTO.TemplateInfo template, SeatLayoutDTO.TemplateConfig config) {
        List<SeatLayout> seats = new ArrayList<>();
        
        int rows = config != null && config.getRows() != null ? config.getRows() : template.getRows();
        int cols = config != null && config.getCols() != null ? config.getCols() : template.getCols();
        String editMode = config != null && config.getEditMode() != null ? config.getEditMode() : "grid";
        String seatPrefix = config != null && config.getSeatPrefix() != null ? config.getSeatPrefix() : "";
        
        // 통로 열 설정
        Set<Integer> aisleColumns = config != null && config.getAisleColumns() != null ? 
                new HashSet<>(config.getAisleColumns()) : 
                Set.of(cols / 2); // 기본적으로 중앙에 통로

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // 통로 건너뛰기
                if (aisleColumns.contains(col)) continue;

                // 좌표 계산 (모드에 따라)
                int x, y;
                if ("grid".equals(editMode)) {
                    x = col + 2; // 왼쪽 여백
                    y = row + 4; // 무대 아래 여백
                } else {
                    x = (col * 50) + 100; // 자유 배치
                    y = (row * 45) + 150;
                }
                
                // 섹션 결정 (행에 따라)
                int section = determineSectionByPosition(row, rows, config);
                
                // 좌석 타입 결정
                SeatLayout.SeatType seatType = determineSeatTypeByPosition(row, rows, config);
                
                // 가격 결정
                int price = determinePriceByType(seatType);
                
                // 좌석 라벨 생성
                String label = generateSeatLabel(row, col, seatPrefix);

                SeatLayout seat = SeatLayout.builder()
                        .venue(venue)
                        .xPosition(x)
                        .yPosition(y)
                        .seatType(seatType)
                        .sectionId(section)
                        .sectionName(section + "구역")
                        .sectionColor(SECTION_COLORS[(section - 1) % SECTION_COLORS.length])
                        .seatLabel(label)
                        .price(price)
                        .isActive(true)
                        .rotation(0)
                        .rowNumber(row + 1)
                        .seatNumber(col + 1)
                        .build();

                seats.add(seat);
            }
        }

        return seats;
    }

    /**
     * 좌석 라벨 생성
     */
    private String generateSeatLabel(int row, int col, String prefix) {
        String rowLabel = String.valueOf((char)('A' + row));
        return prefix + rowLabel + (col + 1);
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
        } else if (config != null && config.getIncludeWheelchairSection() != null && config.getIncludeWheelchairSection() && row == totalRows - 1) {
            return SeatLayout.SeatType.WHEELCHAIR;
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
        
        for (Map.Entry<Integer, List<SeatLayout>> entry : sectionMap.entrySet()) {
            Integer sectionId = entry.getKey();
            List<SeatLayout> sectionSeats = entry.getValue();
            
            // 섹션 이름과 색상 가져오기 (첫 번째 좌석에서)
            SeatLayout firstSeat = sectionSeats.get(0);
            String sectionName = firstSeat.getSectionName() != null ? 
                    firstSeat.getSectionName() : sectionId + "구역";
            String sectionColor = firstSeat.getSectionColor() != null ? 
                    firstSeat.getSectionColor() : SECTION_COLORS[(sectionId - 1) % SECTION_COLORS.length];
            
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

        // 섹션 ID 순으로 정렬
        sections.sort(Comparator.comparing(SeatLayoutDTO.SectionInfo::getId));

        return sections;
    }

    // =========================
    // 레거시 호환성 메서드들
    // =========================

    /**
     * 레거시 유연한 좌석 배치 조회 (호환성 유지)
     */
    @Deprecated
    public SeatLayoutDTO.VenueLayoutResponse getFlexibleSeatMap(Long venueId) {
        SeatLayoutDTO.VenueLayoutResponse response = getVenueLayout(venueId);
        // editMode를 free로 설정
        response.setEditMode("free");
        return response;
    }

    /**
     * 레거시 유연한 좌석 배치 저장 (호환성 유지)
     */
    @Deprecated
    @Transactional
    public SeatLayoutDTO.VenueLayoutResponse updateFlexibleLayout(Map<String, Object> requestMap) {
        // 요청 맵에서 데이터 추출
        Long venueId = Long.valueOf(requestMap.get("venueId").toString());
        
        // 통합된 요청 객체로 변환
        SeatLayoutDTO.VenueLayoutRequest request = SeatLayoutDTO.VenueLayoutRequest.builder()
                .editMode("free")
                .build();
        
        // seats 변환 로직 추가 (필요시)
        
        return saveVenueLayout(venueId, request);
    }
}