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
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class SeatLayoutController {

    private final SeatLayoutService seatLayoutService;

    /**
     * 특정 공연장의 좌석 배치 조회
     */
    @GetMapping("/venues/{venueId}/seat-layouts")
    public ResponseEntity<?> getSeatLayoutsByVenue(@PathVariable Long venueId) {
        try {
            List<SeatLayoutDTO.SeatLayoutResponse> seatLayouts = 
                    seatLayoutService.getSeatLayoutsByVenue(venueId);
            return ResponseEntity.ok(seatLayouts);
        } catch (Exception e) {
            log.error("좌석 배치 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 공연장의 활성 좌석 배치만 조회
     */
    @GetMapping("/venues/{venueId}/seat-layouts/active")
    public ResponseEntity<?> getActiveSeatLayoutsByVenue(@PathVariable Long venueId) {
        try {
            List<SeatLayoutDTO.SeatLayoutSimple> seatLayouts = 
                    seatLayoutService.getActiveSeatLayoutsByVenue(venueId);
            return ResponseEntity.ok(seatLayouts);
        } catch (Exception e) {
            log.error("활성 좌석 배치 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("활성 좌석 배치 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 좌석 맵 전체 조회 (행렬 형태)
     */
    @GetMapping("/venues/{venueId}/seat-map")
    public ResponseEntity<?> getVenueSeatMap(@PathVariable Long venueId) {
        try {
            SeatLayoutDTO.VenueSeatMap seatMap = seatLayoutService.getVenueSeatMap(venueId);
            return ResponseEntity.ok(seatMap);
        } catch (Exception e) {
            log.error("좌석 맵 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 맵 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 단일 등록 (관리자만)
     */
    @PostMapping("/seat-layouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSeatLayout(@Valid @RequestBody SeatLayoutDTO.SeatLayoutRequest request) {
        try {
            SeatLayoutDTO.SeatLayoutResponse seatLayout = seatLayoutService.createSeatLayout(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(seatLayout);
        } catch (Exception e) {
            log.error("좌석 배치 등록 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 등록 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 일괄 등록 (관리자만)
     */
    @PostMapping("/seat-layouts/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSeatLayoutsBulk(@Valid @RequestBody SeatLayoutDTO.SeatLayoutBulkRequest request) {
        try {
            List<SeatLayoutDTO.SeatLayoutResponse> seatLayouts = 
                    seatLayoutService.createSeatLayoutsBulk(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(seatLayouts);
        } catch (Exception e) {
            log.error("좌석 배치 일괄 등록 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 일괄 등록 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 수정 (관리자만)
     */
    @PutMapping("/seat-layouts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSeatLayout(@PathVariable Long id,
                                             @Valid @RequestBody SeatLayoutDTO.SeatLayoutRequest request) {
        try {
            SeatLayoutDTO.SeatLayoutResponse seatLayout = seatLayoutService.updateSeatLayout(id, request);
            return ResponseEntity.ok(seatLayout);
        } catch (Exception e) {
            log.error("좌석 배치 수정 실패: ID = {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 수정 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 삭제 (관리자만)
     */
    @DeleteMapping("/seat-layouts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSeatLayout(@PathVariable Long id) {
        try {
            seatLayoutService.deleteSeatLayout(id);
            return ResponseEntity.ok(new SuccessResponse("좌석 배치가 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("좌석 배치 삭제 실패: ID = {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 삭제 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 좌석 배치 통계 조회
     */
    @GetMapping("/venues/{venueId}/seat-statistics")
    public ResponseEntity<?> getSeatLayoutStatistics(@PathVariable Long venueId) {
        try {
            SeatLayoutDTO.SeatLayoutStatistics statistics = 
                    seatLayoutService.getSeatLayoutStatistics(venueId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("좌석 배치 통계 조회 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("통계 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 좌석 배치 일괄 삭제 (관리자만) - 특정 공연장의 모든 좌석
     */
    @DeleteMapping("/venues/{venueId}/seat-layouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllSeatLayoutsForVenue(@PathVariable Long venueId) {
        try {
            List<SeatLayoutDTO.SeatLayoutResponse> existingLayouts = 
                    seatLayoutService.getSeatLayoutsByVenue(venueId);
            
            for (SeatLayoutDTO.SeatLayoutResponse layout : existingLayouts) {
                seatLayoutService.deleteSeatLayout(layout.getId());
            }
            
            return ResponseEntity.ok(new SuccessResponse(
                    String.format("공연장의 모든 좌석 배치가 삭제되었습니다 (%d개)", existingLayouts.size())));
        } catch (Exception e) {
            log.error("공연장 좌석 배치 일괄 삭제 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 일괄 삭제 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 기본 좌석 배치 자동 생성 (관리자만)
     */
    @PostMapping("/venues/{venueId}/seat-layouts/auto-generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> autoGenerateSeatLayouts(@PathVariable Long venueId) {
        try {
            // 공연장 정보를 기반으로 기본 좌석 배치 자동 생성
            SeatLayoutDTO.SeatLayoutBulkRequest autoRequest = SeatLayoutDTO.SeatLayoutBulkRequest.builder()
                    .venueId(venueId)
                    .startRow(1)
                    .endRow(10) // 기본 10행
                    .seatsPerRow(10) // 기본 10석
                    .seatType(com.springproject.stbookingsystem.entity.SeatLayout.SeatType.REGULAR)
                    .isActive(true)
                    .build();

            List<SeatLayoutDTO.SeatLayoutResponse> seatLayouts = 
                    seatLayoutService.createSeatLayoutsBulk(autoRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new AutoGenerateResponse(
                    "기본 좌석 배치가 자동 생성되었습니다", seatLayouts.size(), seatLayouts));
        } catch (Exception e) {
            log.error("좌석 배치 자동 생성 실패: venueId = {}", venueId, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 배치 자동 생성 실패: " + e.getMessage()));
        }
    }

    // 응답 클래스들
    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class AutoGenerateResponse {
        private String message;
        private int generatedCount;
        private List<SeatLayoutDTO.SeatLayoutResponse> seatLayouts;

        public AutoGenerateResponse(String message, int generatedCount, 
                                  List<SeatLayoutDTO.SeatLayoutResponse> seatLayouts) {
            this.message = message;
            this.generatedCount = generatedCount;
            this.seatLayouts = seatLayouts;
        }

        public String getMessage() {
            return message;
        }

        public int getGeneratedCount() {
            return generatedCount;
        }

        public List<SeatLayoutDTO.SeatLayoutResponse> getSeatLayouts() {
            return seatLayouts;
        }
    }
}
