package com.springproject.stbookingsystem.controller;

import com.springproject.stbookingsystem.dto.SeatLayoutDTO;
import com.springproject.stbookingsystem.service.SeatLayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/seat-layouts")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class SeatLayoutController {

    private final SeatLayoutService seatLayoutService;

    /**
     * 공연장 좌석 배치 조회 (통합 버전)
     */
    @GetMapping("/venues/{venueId}")
    public ResponseEntity<?> getVenueSeatLayout(@PathVariable Long venueId) {
        try {
            log.info("좌석 배치 조회 요청: venueId = {}", venueId);
            SeatLayoutDTO.VenueLayoutResponse layout = seatLayoutService.getVenueLayout(venueId);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("좌석 배치 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 저장 (통합 버전)
     */
    @PostMapping("/venues/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveVenueSeatLayout(
            @PathVariable Long venueId,
            @Valid @RequestBody SeatLayoutDTO.VenueLayoutRequest request) {
        try {
            log.info("좌석 배치 저장 요청: venueId = {}, 좌석 수 = {}", 
                    venueId, request.getSeats() != null ? request.getSeats().size() : 0);
            
            SeatLayoutDTO.VenueLayoutResponse layout = seatLayoutService.saveVenueLayout(venueId, request);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("좌석 배치 저장 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 저장 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 템플릿 적용
     */
    @PostMapping("/venues/{venueId}/templates/{templateName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> applyTemplate(
            @PathVariable Long venueId, 
            @PathVariable String templateName,
            @RequestBody(required = false) SeatLayoutDTO.TemplateConfig config) {
        try {
            log.info("템플릿 적용 요청: venueId = {}, template = {}", venueId, templateName);
            
            SeatLayoutDTO.VenueLayoutResponse layout = seatLayoutService.applyTemplate(venueId, templateName, config);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("템플릿 적용 실패: venueId = {}, template = {}", venueId, templateName, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("템플릿 적용 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 초기화
     */
    @DeleteMapping("/venues/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearVenueSeatLayout(@PathVariable Long venueId) {
        try {
            log.info("좌석 배치 초기화 요청: venueId = {}", venueId);
            
            seatLayoutService.clearVenueLayout(venueId);
            return ResponseEntity.ok(new SuccessResponse("좌석 배치가 초기화되었습니다"));
        } catch (Exception e) {
            log.error("좌석 배치 초기화 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 초기화 실패: " + e.getMessage()));
        }
    }

    /**
     * 사용 가능한 템플릿 목록 조회
     */
    @GetMapping("/templates")
    public ResponseEntity<?> getAvailableTemplates() {
        try {
            log.info("템플릿 목록 조회 요청");
            
            List<SeatLayoutDTO.TemplateInfo> templates = seatLayoutService.getAvailableTemplates();
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("템플릿 목록 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("템플릿 목록 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 통계 조회
     */
    @GetMapping("/venues/{venueId}/statistics")
    public ResponseEntity<?> getVenueStatistics(@PathVariable Long venueId) {
        try {
            log.info("통계 조회 요청: venueId = {}", venueId);
            
            SeatLayoutDTO.VenueStatistics statistics = seatLayoutService.getVenueStatistics(venueId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("통계 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("통계 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 유효성 검사
     */
    @PostMapping("/venues/{venueId}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> validateSeatLayout(
            @PathVariable Long venueId,
            @Valid @RequestBody SeatLayoutDTO.VenueLayoutRequest request) {
        try {
            log.info("좌석 배치 유효성 검사 요청: venueId = {}", venueId);
            
            // 유효성 검사 로직
            ValidationResult result = validateLayoutRequest(request);
            
            if (result.isValid()) {
                return ResponseEntity.ok(new ValidationResponse(true, "유효한 좌석 배치입니다", result.getWarnings()));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ValidationResponse(false, "유효하지 않은 좌석 배치입니다", result.getErrors()));
            }
        } catch (Exception e) {
            log.error("유효성 검사 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("유효성 검사 실패: " + e.getMessage()));
        }
    }

    /**
     * 템플릿 미리보기
     */
    @GetMapping("/templates/{templateName}/preview")
    public ResponseEntity<?> getTemplatePreview(
            @PathVariable String templateName,
            @RequestParam(required = false) Integer rows,
            @RequestParam(required = false) Integer cols,
            @RequestParam(required = false, defaultValue = "grid") String editMode) {
        try {
            log.info("템플릿 미리보기 요청: template = {}, rows = {}, cols = {}", templateName, rows, cols);
            
            // 미리보기 설정
            SeatLayoutDTO.TemplateConfig config = SeatLayoutDTO.TemplateConfig.builder()
                    .rows(rows)
                    .cols(cols)
                    .editMode(editMode)
                    .build();
            
            // 임시 VenueId로 미리보기 생성 (실제 저장하지 않음)
            TemplatePreview preview = generateTemplatePreview(templateName, config);
            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            log.error("템플릿 미리보기 실패: template = {}", templateName, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("템플릿 미리보기 실패: " + e.getMessage()));
        }
    }

    // =========================
    // 레거시 호환성 엔드포인트
    // =========================

    /**
     * 레거시 유연한 좌석 배치 조회 (호환성 유지)
     */
    @Deprecated
    @GetMapping("/venues/{venueId}/flexible")
    public ResponseEntity<?> getFlexibleSeatMap(@PathVariable Long venueId) {
        try {
            log.info("레거시 유연한 좌석 배치 조회: venueId = {}", venueId);
            SeatLayoutDTO.VenueLayoutResponse layout = seatLayoutService.getFlexibleSeatMap(venueId);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("레거시 좌석 배치 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 레거시 유연한 좌석 배치 저장 (호환성 유지)
     */
    @Deprecated
    @PostMapping("/venues/{venueId}/flexible")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFlexibleLayout(
            @PathVariable Long venueId,
            @RequestBody Map<String, Object> request) {
        try {
            log.info("레거시 유연한 좌석 배치 저장: venueId = {}", venueId);
            request.put("venueId", venueId);
            SeatLayoutDTO.VenueLayoutResponse layout = seatLayoutService.updateFlexibleLayout(request);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("레거시 좌석 배치 저장 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 저장 실패: " + e.getMessage()));
        }
    }

    // =========================
    // 헬퍼 메서드들
    // =========================

    /**
     * 좌석 배치 유효성 검사
     */
    private ValidationResult validateLayoutRequest(SeatLayoutDTO.VenueLayoutRequest request) {
        ValidationResult result = new ValidationResult();
        
        if (request.getSeats() == null || request.getSeats().isEmpty()) {
            result.addError("좌석이 없습니다");
            return result;
        }

        // 좌석 겹침 검사
        Map<String, Long> positionCounts = request.getSeats().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    seat -> seat.getX() + "," + seat.getY(),
                    java.util.stream.Collectors.counting()
                ));
        
        positionCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> result.addError("위치 " + entry.getKey() + "에 " + entry.getValue() + "개의 좌석이 겹칩니다"));

        // 캔버스 범위 검사
        if (request.getCanvas() != null) {
            request.getSeats().stream()
                    .filter(seat -> seat.getX() < 0 || seat.getY() < 0 || 
                                  seat.getX() >= request.getCanvas().getWidth() || 
                                  seat.getY() >= request.getCanvas().getHeight())
                    .forEach(seat -> result.addError("좌석 " + seat.getLabel() + "이 캔버스 범위를 벗어났습니다"));
        }

        // 경고사항
        long totalSeats = request.getSeats().size();
        if (totalSeats > 1000) {
            result.addWarning("좌석 수가 매우 많습니다 (" + totalSeats + "개). 성능에 영향을 줄 수 있습니다.");
        }

        return result;
    }

    /**
     * 템플릿 미리보기 생성
     */
    private TemplatePreview generateTemplatePreview(String templateName, SeatLayoutDTO.TemplateConfig config) {
        // 템플릿 정보 조회
        List<SeatLayoutDTO.TemplateInfo> templates = seatLayoutService.getAvailableTemplates();
        SeatLayoutDTO.TemplateInfo template = templates.stream()
                .filter(t -> t.getName().equals(templateName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("템플릿을 찾을 수 없습니다: " + templateName));
        
        // 미리보기 데이터 생성
        int rows = config.getRows() != null ? config.getRows() : template.getRows();
        int cols = config.getCols() != null ? config.getCols() : template.getCols();
        int estimatedSeats = (int) (rows * cols * 0.85); // 통로 고려
        
        return TemplatePreview.builder()
                .templateName(templateName)
                .displayName(template.getDisplayName())
                .description(template.getDescription())
                .rows(rows)
                .cols(cols)
                .estimatedSeats(estimatedSeats)
                .estimatedRevenue(estimatedSeats * 50000) // 기본 가격
                .editMode(config.getEditMode())
                .build();
    }

    // =========================
    // 응답 클래스들
    // =========================

    record ErrorResponse(String message) {}
    record SuccessResponse(String message) {}

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ValidationResponse {
        private boolean valid;
        private String message;
        private List<String> issues;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class TemplatePreview {
        private String templateName;
        private String displayName;
        private String description;
        private Integer rows;
        private Integer cols;
        private Integer estimatedSeats;
        private Integer estimatedRevenue;
        private String editMode;
    }

    static class ValidationResult {
        private final List<String> errors = new java.util.ArrayList<>();
        private final List<String> warnings = new java.util.ArrayList<>();
        
        public void addError(String error) { errors.add(error); }
        public void addWarning(String warning) { warnings.add(warning); }
        public boolean isValid() { return errors.isEmpty(); }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
    }
}