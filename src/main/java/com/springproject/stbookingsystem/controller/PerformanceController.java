package com.springproject.stbookingsystem.controller;


import com.springproject.stbookingsystem.dto.PerformanceDTO;
import com.springproject.stbookingsystem.sevice.PerformanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performances")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    /**
     * 모든 공연 조회
     */
    @GetMapping
    public ResponseEntity<List<PerformanceDTO.PerformanceResponse>> getAllPerformances() {
        List<PerformanceDTO.PerformanceResponse> performances = performanceService.getAllPerformances();
        return ResponseEntity.ok(performances);
    }

    /**
     * 공연 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPerformanceById(@PathVariable Long id) {
        try {
            PerformanceDTO.PerformanceResponse performance = performanceService.getPerformanceById(id);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연 등록 (관리자만)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPerformance(@Valid @RequestBody PerformanceDTO.PerformanceRequest request) {
        try {
            PerformanceDTO.PerformanceResponse performance = performanceService.createPerformance(request);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연 등록 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연 수정 (관리자만)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePerformance(@PathVariable Long id,
                                               @Valid @RequestBody PerformanceDTO.PerformanceRequest request) {
        try {
            PerformanceDTO.PerformanceResponse performance = performanceService.updatePerformance(id, request);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연 수정 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연 삭제 (관리자만)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePerformance(@PathVariable Long id) {
        try {
            performanceService.deletePerformance(id);
            return ResponseEntity.ok(new SuccessResponse("공연이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연 삭제 실패: " + e.getMessage()));
        }
    }

    /**
     * 공연 검색
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPerformances(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("검색어를 입력해주세요"));
            }

            List<PerformanceDTO.PerformanceResponse> performances =
                    performanceService.searchPerformances(keyword.trim());
            return ResponseEntity.ok(performances);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("공연 검색 실패: " + e.getMessage()));
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
}
