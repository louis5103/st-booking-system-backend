package com.springproject.stbookingsystem.controller;

import com.springproject.stbookingsystem.dto.VenueDTO;
import com.springproject.stbookingsystem.service.VenueService;
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
public class VenueController {

    private final VenueService venueService;

    /**
     * 모든 공연장 조회
     */
    @GetMapping("/venues")
    public ResponseEntity<?> getAllVenues() {
        try {
            List<VenueDTO.VenueResponse> venues = venueService.getAllVenues();
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            log.error("공연장 목록 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연장 목록 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 상세 조회
     */
    @GetMapping("/venues/{id}")
    public ResponseEntity<?> getVenueById(@PathVariable Long id) {
        try {
            VenueDTO.VenueResponse venue = venueService.getVenueById(id);
            return ResponseEntity.ok(venue);
        } catch (Exception e) {
            log.error("공연장 조회 실패: ID = {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연장 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 등록 (관리자만)
     */
    @PostMapping("/venues")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createVenue(@Valid @RequestBody VenueDTO.VenueRequest request) {
        try {
            VenueDTO.VenueResponse venue = venueService.createVenue(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(venue);
        } catch (Exception e) {
            log.error("공연장 등록 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연장 등록 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 정보 수정 (관리자만)
     */
    @PutMapping("/venues/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVenue(@PathVariable Long id, 
                                        @Valid @RequestBody VenueDTO.VenueRequest request) {
        try {
            VenueDTO.VenueResponse venue = venueService.updateVenue(id, request);
            return ResponseEntity.ok(venue);
        } catch (Exception e) {
            log.error("공연장 수정 실패: ID = {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연장 수정 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 좌석 구조 수정 (관리자만)
     */
    @PutMapping("/venues/{id}/seat-structure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVenueSeatStructure(@PathVariable Long id,
                                                     @Valid @RequestBody VenueDTO.VenueSeatStructureRequest request) {
        try {
            VenueDTO.VenueResponse venue = venueService.updateVenueSeatStructure(id, request);
            return ResponseEntity.ok(venue);
        } catch (Exception e) {
            log.error("공연장 좌석 구조 수정 실패: ID = {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("좌석 구조 수정 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 삭제 (관리자만)
     */
    @DeleteMapping("/venues/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVenue(@PathVariable Long id) {
        try {
            venueService.deleteVenue(id);
            return ResponseEntity.ok(new SuccessResponse("공연장이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("공연장 삭제 실패: ID = {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연장 삭제 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 검색
     */
    @PostMapping("/venues/search")
    public ResponseEntity<?> searchVenues(@RequestBody VenueDTO.VenueSearchRequest searchRequest) {
        try {
            List<VenueDTO.VenueSimple> venues = venueService.searchVenues(searchRequest);
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            log.error("공연장 검색 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연장 검색 실패: " + e.getMessage()));
        }
    }

    /**
     * 활성 공연이 있는 공연장 목록 조회
     */
    @GetMapping("/venues/active")
    public ResponseEntity<?> getVenuesWithActivePerformances() {
        try {
            List<VenueDTO.VenueSimple> venues = venueService.getVenuesWithActivePerformances();
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            log.error("활성 공연장 목록 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("활성 공연장 목록 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장 통계 조회 (관리자만)
     */
    @GetMapping("/venues/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getVenueStatistics() {
        try {
            List<VenueDTO.VenueStatistics> statistics = venueService.getVenueStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("공연장 통계 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("통계 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연장명 중복 확인
     */
    @GetMapping("/venues/check-name")
    public ResponseEntity<?> checkVenueName(@RequestParam String name) {
        try {
            // 간단한 중복 확인 로직
            boolean exists = venueService.getAllVenues().stream()
                    .anyMatch(venue -> venue.getName().equals(name));
            return ResponseEntity.ok(new CheckResponse("name", exists));
        } catch (Exception e) {
            log.error("공연장명 중복 확인 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("중복 확인 실패: " + e.getMessage()));
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

    static class CheckResponse {
        private String field;
        private boolean exists;

        public CheckResponse(String field, boolean exists) {
            this.field = field;
            this.exists = exists;
        }

        public String getField() {
            return field;
        }

        public boolean isExists() {
            return exists;
        }
    }
}
