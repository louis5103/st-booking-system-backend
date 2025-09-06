package com.springproject.stbookingsystem.service;

import com.springproject.stbookingsystem.dto.PerformanceDTO;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.entity.Venue;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
import com.springproject.stbookingsystem.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    /**
     * 모든 공연 조회
     */
    @Transactional(readOnly = true)
    public List<PerformanceDTO.PerformanceResponse> getAllPerformances() {
        return performanceRepository.findAllByOrderByPerformanceDateAsc()
                .stream()
                .map(PerformanceDTO.PerformanceResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 공연 ID로 조회
     */
    @Transactional(readOnly = true)
    public PerformanceDTO.PerformanceResponse getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));
        return PerformanceDTO.PerformanceResponse.from(performance);
    }

    /**
     * 공연 등록
     */
    public PerformanceDTO.PerformanceResponse createPerformance(PerformanceDTO.PerformanceRequest request) {
        // 공연장 조회
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + request.getVenueId()));

        Performance performance = Performance.builder()
                .title(request.getTitle())
                .venue(venue)
                .venueName(venue.getName())
                .performanceDate(request.getPerformanceDate())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats() != null ? request.getTotalSeats() : venue.getTotalSeats())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        Performance savedPerformance = performanceRepository.save(performance);

        // 좌석 자동 생성 (공연장의 좌석 배치 기준)
        createSeatsForPerformance(savedPerformance, venue);

        return PerformanceDTO.PerformanceResponse.from(savedPerformance);
    }

    /**
     * 공연 수정
     */
    public PerformanceDTO.PerformanceResponse updatePerformance(Long id, PerformanceDTO.PerformanceRequest request) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        // 공연장 조회 (변경된 경우에만)
        Venue venue = null;
        if (request.getVenueId() != null && 
            (performance.getVenue() == null || !performance.getVenue().getId().equals(request.getVenueId()))) {
            venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + request.getVenueId()));
        } else {
            venue = performance.getVenue();
        }

        // 좌석 수가 변경되었고 이미 예매가 있는 경우 체크
        Integer newTotalSeats = request.getTotalSeats() != null ? request.getTotalSeats() : venue.getTotalSeats();
        if (!performance.getTotalSeats().equals(newTotalSeats)) {
            Long bookedSeats = seatRepository.countBookedSeatsByPerformanceId(id);
            if (bookedSeats > 0 && newTotalSeats < bookedSeats) {
                throw new RuntimeException("이미 예매된 좌석 수보다 적게 설정할 수 없습니다");
            }
        }

        performance.setTitle(request.getTitle());
        performance.setVenue(venue);
        performance.setVenueName(venue.getName());
        performance.setPerformanceDate(request.getPerformanceDate());
        performance.setPrice(request.getPrice());
        performance.setDescription(request.getDescription());
        performance.setImageUrl(request.getImageUrl());

        // 좌석 수가 변경된 경우 좌석 재생성
        if (!performance.getTotalSeats().equals(newTotalSeats)) {
            performance.setTotalSeats(newTotalSeats);
            updateSeatsForPerformance(performance, venue);
        }

        Performance savedPerformance = performanceRepository.save(performance);
        return PerformanceDTO.PerformanceResponse.from(savedPerformance);
    }

    /**
     * 공연 삭제
     */
    public void deletePerformance(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        // 예매된 좌석이 있는지 확인
        Long bookedSeats = seatRepository.countBookedSeatsByPerformanceId(id);
        if (bookedSeats > 0) {
            throw new RuntimeException("예매된 좌석이 있는 공연은 삭제할 수 없습니다");
        }

        performanceRepository.delete(performance);
    }

    /**
     * 공연 검색 (제목 또는 장소)
     */
    @Transactional(readOnly = true)
    public List<PerformanceDTO.PerformanceResponse> searchPerformances(String keyword) {
        return performanceRepository.findByTitleOrVenueContaining(keyword)
                .stream()
                .map(PerformanceDTO.PerformanceResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 공연에 대한 좌석 자동 생성 (공연장 구조 기반)
     */
    private void createSeatsForPerformance(Performance performance, Venue venue) {
        // 공연장의 좌석 배치가 있는 경우, 해당 배치 기준으로 생성
        // 없는 경우 기본 구조로 생성
        
        if (venue.getSeatLayouts() != null && !venue.getSeatLayouts().isEmpty()) {
            // 공연장의 좌석 배치 기준으로 좌석 생성
            for (com.springproject.stbookingsystem.entity.SeatLayout seatLayout : venue.getSeatLayouts()) {
                if (seatLayout.getIsActive() && seatLayout.isBookable()) {
                    Seat seat = Seat.builder()
                            .performance(performance)
                            .seatNumber(seatLayout.getSeatLabel())
                            .rowNumber(seatLayout.getRowNumber())
                            .seatInRow(seatLayout.getSeatNumber())
                            .seatLayout(seatLayout)
                            .build();
                    seatRepository.save(seat);
                }
            }
        } else {
            // 기본 구조로 좌석 생성 (기존 방식)
            int totalSeats = performance.getTotalSeats();
            int seatsPerRow = venue.getSeatsPerRow();

            for (int i = 1; i <= totalSeats; i++) {
                int row = ((i - 1) / seatsPerRow) + 1;
                int seatInRow = ((i - 1) % seatsPerRow) + 1;
                String seatName = venue.generateSeatNumber(row, seatInRow);

                Seat seat = Seat.builder()
                        .performance(performance)
                        .seatNumber(seatName)
                        .rowNumber(row)
                        .seatInRow(seatInRow)
                        .build();
                seatRepository.save(seat);
            }
        }
    }

    /**
     * 공연 좌석 수 변경 시 좌석 업데이트
     */
    private void updateSeatsForPerformance(Performance performance, Venue venue) {
        // 기존 예매되지 않은 좌석들 삭제
        List<Seat> existingSeats = seatRepository.findByPerformanceOrderBySeatNumberAsc(performance);
        List<Seat> unbookedSeats = existingSeats.stream()
                .filter(seat -> !seat.getIsBooked())
                .collect(Collectors.toList());

        seatRepository.deleteAll(unbookedSeats);

        // 현재 예매된 좌석 수
        int currentSeatCount = existingSeats.size() - unbookedSeats.size();

        // 필요한 만큼 새 좌석 추가
        int targetSeats = performance.getTotalSeats();
        if (targetSeats > currentSeatCount) {
            int seatsToAdd = targetSeats - currentSeatCount;
            int seatsPerRow = venue.getSeatsPerRow();
            int startNumber = currentSeatCount + 1;

            for (int i = 0; i < seatsToAdd; i++) {
                int seatIndex = startNumber + i;
                int row = ((seatIndex - 1) / seatsPerRow) + 1;
                int seatInRow = ((seatIndex - 1) % seatsPerRow) + 1;
                String seatName = venue.generateSeatNumber(row, seatInRow);

                Seat seat = Seat.builder()
                        .performance(performance)
                        .seatNumber(seatName)
                        .rowNumber(row)
                        .seatInRow(seatInRow)
                        .build();
                seatRepository.save(seat);
            }
        }
    }
}
