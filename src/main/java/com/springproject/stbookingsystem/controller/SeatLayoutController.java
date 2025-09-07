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

@Slf4j
@RestController
@RequestMapping("/api/seat-layouts")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class SeatLayoutController {

    private final SeatLayoutService seatLayoutService;

    /**
     * 공연장 좌석 배치 조회
     */
    @GetMapping("/venues/{venueId}")
    public ResponseEntity<?> getVenueSeatLayout(@PathVariable Long venueId) {
        try {
            SeatLayoutDTO.VenueLayoutResponse layout = seatLayoutService.getVenueLayout(venueId);
            return ResponseEntity.ok(layout);
        } catch (Exception e) {
            log.error("좌석 배치 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 저장 (전체 업데이트)
     */
    @PostMapping("/venues/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveVenueSeatLayout(
            @PathVariable Long venueId,
            @Valid @RequestBody SeatLayoutDTO.VenueLayoutRequest request) {
        try {
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
            SeatLayoutDTO.VenueStatistics statistics = seatLayoutService.getVenueStatistics(venueId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("통계 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("통계 조회 실패: " + e.getMessage()));
        }
    }

    // 응답 클래스들
    record ErrorResponse(String message) {}
    record SuccessResponse(String message) {}
}
