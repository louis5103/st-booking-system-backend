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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatLayoutService {

    private final SeatLayoutRepository seatLayoutRepository;
    private final VenueRepository venueRepository;

    /**
     * 특정 공연장의 좌석 배치 조회
     */
    public List<SeatLayoutDTO.SeatLayoutResponse> getSeatLayoutsByVenue(Long venueId) {
        log.info("공연장 좌석 배치 조회: venueId = {}", venueId);
        
        return seatLayoutRepository.findByVenueIdOrderByRowNumberAscSeatNumberAsc(venueId)
                .stream()
                .map(SeatLayoutDTO.SeatLayoutResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 공연장의 활성 좌석 배치만 조회
     */
    public List<SeatLayoutDTO.SeatLayoutSimple> getActiveSeatLayoutsByVenue(Long venueId) {
        log.info("공연장 활성 좌석 배치 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        return seatLayoutRepository.findByVenueAndIsActiveTrueOrderByRowNumberAscSeatNumberAsc(venue)
                .stream()
                .map(SeatLayoutDTO.SeatLayoutSimple::from)
                .collect(Collectors.toList());
    }

    /**
     * 공연장 좌석 맵 전체 조회 (행렬 형태)
     */
    public SeatLayoutDTO.VenueSeatMap getVenueSeatMap(Long venueId) {
        log.info("공연장 좌석 맵 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByRowNumberAscSeatNumberAsc(venue);
        
        // 행별로 좌석을 그룹화
        Map<Integer, List<SeatLayoutDTO.SeatLayoutSimple>> rowMap = new HashMap<>();
        
        for (SeatLayout seatLayout : seatLayouts) {
            rowMap.computeIfAbsent(seatLayout.getRowNumber(), k -> new ArrayList<>())
                   .add(SeatLayoutDTO.SeatLayoutSimple.from(seatLayout));
        }

        // 행렬 형태로 변환
        List<List<SeatLayoutDTO.SeatLayoutSimple>> seatMatrix = new ArrayList<>();
        for (int row = 1; row <= venue.getTotalRows(); row++) {
            List<SeatLayoutDTO.SeatLayoutSimple> rowSeats = rowMap.getOrDefault(row, new ArrayList<>());
            seatMatrix.add(rowSeats);
        }

        // 통계 생성
        SeatLayoutDTO.SeatLayoutStatistics statistics = generateStatistics(venue, seatLayouts);

        return SeatLayoutDTO.VenueSeatMap.from(
                venueId, venue.getName(), venue.getTotalRows(), 
                venue.getSeatsPerRow(), seatMatrix, statistics
        );
    }

    /**
     * 좌석 배치 단일 등록
     */
    @Transactional
    public SeatLayoutDTO.SeatLayoutResponse createSeatLayout(SeatLayoutDTO.SeatLayoutRequest request) {
        log.info("좌석 배치 등록: venueId = {}, row = {}, seat = {}", 
                request.getVenueId(), request.getRowNumber(), request.getSeatNumber());

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + request.getVenueId()));

        // 중복 확인
        if (seatLayoutRepository.existsByVenueAndRowNumberAndSeatNumber(
                venue, request.getRowNumber(), request.getSeatNumber())) {
            throw new RuntimeException("이미 존재하는 좌석 위치입니다: " + 
                    request.getRowNumber() + "-" + request.getSeatNumber());
        }

        // 좌석 위치 검증
        validateSeatPosition(venue, request.getRowNumber(), request.getSeatNumber());

        SeatLayout seatLayout = SeatLayout.builder()
                .venue(venue)
                .rowNumber(request.getRowNumber())
                .seatNumber(request.getSeatNumber())
                .seatType(request.getSeatType())
                .isActive(request.getIsActive())
                .description(request.getDescription())
                .xPosition(request.getXPosition())
                .yPosition(request.getYPosition())
                .build();

        // 좌석 레이블 자동 생성
        seatLayout.generateSeatLabel();

        SeatLayout savedSeatLayout = seatLayoutRepository.save(seatLayout);
        log.info("좌석 배치 등록 완료: ID = {}", savedSeatLayout.getId());

        return SeatLayoutDTO.SeatLayoutResponse.from(savedSeatLayout);
    }

    /**
     * 좌석 배치 일괄 등록
     */
    @Transactional
    public List<SeatLayoutDTO.SeatLayoutResponse> createSeatLayoutsBulk(SeatLayoutDTO.SeatLayoutBulkRequest request) {
        log.info("좌석 배치 일괄 등록: venueId = {}, rows = {} ~ {}", 
                request.getVenueId(), request.getStartRow(), request.getEndRow());

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + request.getVenueId()));

        List<SeatLayout> seatLayouts = new ArrayList<>();

        for (int row = request.getStartRow(); row <= request.getEndRow(); row++) {
            for (int seat = 1; seat <= request.getSeatsPerRow(); seat++) {
                // 중복 확인
                if (seatLayoutRepository.existsByVenueAndRowNumberAndSeatNumber(venue, row, seat)) {
                    log.warn("이미 존재하는 좌석 건너뜀: {}-{}", row, seat);
                    continue;
                }

                // 예외 처리 확인
                SeatLayoutDTO.SeatLayoutException exception = findException(request.getExceptions(), row, seat);
                
                SeatLayout seatLayout = SeatLayout.builder()
                        .venue(venue)
                        .rowNumber(row)
                        .seatNumber(seat)
                        .seatType(exception != null ? exception.getSeatType() : request.getSeatType())
                        .isActive(exception != null ? exception.getIsActive() : request.getIsActive())
                        .description(exception != null ? exception.getDescription() : null)
                        .build();

                seatLayout.generateSeatLabel();
                seatLayouts.add(seatLayout);
            }
        }

        List<SeatLayout> savedSeatLayouts = seatLayoutRepository.saveAll(seatLayouts);
        log.info("좌석 배치 일괄 등록 완료: {} 개 좌석", savedSeatLayouts.size());

        return savedSeatLayouts.stream()
                .map(SeatLayoutDTO.SeatLayoutResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 좌석 배치 수정
     */
    @Transactional
    public SeatLayoutDTO.SeatLayoutResponse updateSeatLayout(Long id, SeatLayoutDTO.SeatLayoutRequest request) {
        log.info("좌석 배치 수정: ID = {}", id);

        SeatLayout seatLayout = seatLayoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("좌석 배치를 찾을 수 없습니다: " + id));

        // 위치 변경 시 중복 확인
        if (!seatLayout.getRowNumber().equals(request.getRowNumber()) ||
            !seatLayout.getSeatNumber().equals(request.getSeatNumber())) {
            
            if (seatLayoutRepository.existsByVenueAndRowNumberAndSeatNumber(
                    seatLayout.getVenue(), request.getRowNumber(), request.getSeatNumber())) {
                throw new RuntimeException("이미 존재하는 좌석 위치입니다: " + 
                        request.getRowNumber() + "-" + request.getSeatNumber());
            }

            validateSeatPosition(seatLayout.getVenue(), request.getRowNumber(), request.getSeatNumber());
            seatLayout.setRowNumber(request.getRowNumber());
            seatLayout.setSeatNumber(request.getSeatNumber());
            seatLayout.generateSeatLabel();
        }

        seatLayout.updateSeatInfo(request.getSeatType(), request.getIsActive(), request.getDescription());
        seatLayout.updatePosition(request.getXPosition(), request.getYPosition());

        SeatLayout savedSeatLayout = seatLayoutRepository.save(seatLayout);
        log.info("좌석 배치 수정 완료: ID = {}", savedSeatLayout.getId());

        return SeatLayoutDTO.SeatLayoutResponse.from(savedSeatLayout);
    }

    /**
     * 좌석 배치 삭제
     */
    @Transactional
    public void deleteSeatLayout(Long id) {
        log.info("좌석 배치 삭제: ID = {}", id);

        SeatLayout seatLayout = seatLayoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("좌석 배치를 찾을 수 없습니다: " + id));

        seatLayoutRepository.delete(seatLayout);
        log.info("좌석 배치 삭제 완료: ID = {}", id);
    }

    /**
     * 공연장 좌석 배치 통계
     */
    public SeatLayoutDTO.SeatLayoutStatistics getSeatLayoutStatistics(Long venueId) {
        log.info("좌석 배치 통계 조회: venueId = {}", venueId);

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + venueId));

        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueOrderByRowNumberAscSeatNumberAsc(venue);
        
        return generateStatistics(venue, seatLayouts);
    }

    // 비공개 메소드들

    /**
     * 좌석 위치 검증
     */
    private void validateSeatPosition(Venue venue, Integer rowNumber, Integer seatNumber) {
        if (rowNumber <= 0 || rowNumber > venue.getTotalRows()) {
            throw new RuntimeException("유효하지 않은 행 번호입니다: " + rowNumber);
        }
        if (seatNumber <= 0 || seatNumber > venue.getSeatsPerRow()) {
            throw new RuntimeException("유효하지 않은 좌석 번호입니다: " + seatNumber);
        }
    }

    /**
     * 예외 처리 좌석 찾기
     */
    private SeatLayoutDTO.SeatLayoutException findException(List<SeatLayoutDTO.SeatLayoutException> exceptions, 
                                                          int row, int seat) {
        if (exceptions == null) return null;
        
        return exceptions.stream()
                .filter(ex -> ex.getRowNumber().equals(row) && ex.getSeatNumber().equals(seat))
                .findFirst()
                .orElse(null);
    }

    /**
     * 좌석 배치 통계 생성
     */
    private SeatLayoutDTO.SeatLayoutStatistics generateStatistics(Venue venue, List<SeatLayout> seatLayouts) {
        SeatLayoutDTO.SeatLayoutStatistics stats = SeatLayoutDTO.SeatLayoutStatistics.create(
                venue.getId(), venue.getName());

        stats.setTotalSeats(seatLayouts.size());
        stats.setActiveSeats((int) seatLayouts.stream().filter(SeatLayout::getIsActive).count());
        stats.setBookableSeats((int) seatLayouts.stream().filter(SeatLayout::isBookable).count());

        // 타입별 카운트
        Map<SeatLayout.SeatType, Long> typeCounts = seatLayouts.stream()
                .collect(Collectors.groupingBy(SeatLayout::getSeatType, Collectors.counting()));

        stats.setRegularSeats(typeCounts.getOrDefault(SeatLayout.SeatType.REGULAR, 0L).intValue());
        stats.setVipSeats(typeCounts.getOrDefault(SeatLayout.SeatType.VIP, 0L).intValue());
        stats.setPremiumSeats(typeCounts.getOrDefault(SeatLayout.SeatType.PREMIUM, 0L).intValue());
        stats.setWheelchairSeats(typeCounts.getOrDefault(SeatLayout.SeatType.WHEELCHAIR, 0L).intValue());
        stats.setBlockedSeats(typeCounts.getOrDefault(SeatLayout.SeatType.BLOCKED, 0L).intValue());
        stats.setAisleSpaces(typeCounts.getOrDefault(SeatLayout.SeatType.AISLE, 0L).intValue());
        stats.setStageAreas(typeCounts.getOrDefault(SeatLayout.SeatType.STAGE, 0L).intValue());

        return stats;
    }
}
