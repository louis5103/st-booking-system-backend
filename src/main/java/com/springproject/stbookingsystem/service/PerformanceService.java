package com.springproject.stbookingsystem.service;

import com.springproject.stbookingsystem.dto.PerformanceDTO;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
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
        Performance performance = Performance.builder()
                .title(request.getTitle())
                .venue(request.getVenue())
                .performanceDate(request.getPerformanceDate())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        Performance savedPerformance = performanceRepository.save(performance);

        // 좌석 자동 생성
        createSeatsForPerformance(savedPerformance);

        return PerformanceDTO.PerformanceResponse.from(savedPerformance);
    }

    /**
     * 공연 수정
     */
    public PerformanceDTO.PerformanceResponse updatePerformance(Long id, PerformanceDTO.PerformanceRequest request) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        // 좌석 수가 변경되었고 이미 예매가 있는 경우 체크
        if (!performance.getTotalSeats().equals(request.getTotalSeats())) {
            Long bookedSeats = seatRepository.countBookedSeatsByPerformanceId(id);
            if (bookedSeats > 0 && request.getTotalSeats() < bookedSeats) {
                throw new RuntimeException("이미 예매된 좌석 수보다 적게 설정할 수 없습니다");
            }
        }

        performance.setTitle(request.getTitle());
        performance.setVenue(request.getVenue());
        performance.setPerformanceDate(request.getPerformanceDate());
        performance.setPrice(request.getPrice());
        performance.setDescription(request.getDescription());
        performance.setImageUrl(request.getImageUrl());

        // 좌석 수가 변경된 경우 좌석 재생성
        if (!performance.getTotalSeats().equals(request.getTotalSeats())) {
            performance.setTotalSeats(request.getTotalSeats());
            updateSeatsForPerformance(performance);
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
     * 공연에 대한 좌석 자동 생성
     */
    private void createSeatsForPerformance(Performance performance) {
        int totalSeats = performance.getTotalSeats();
        int seatsPerRow = 10; // 한 행당 10석

        for (int i = 1; i <= totalSeats; i++) {
            char row = (char) ('A' + (i - 1) / seatsPerRow);
            int seatNumber = ((i - 1) % seatsPerRow) + 1;
            String seatName = row + String.valueOf(seatNumber);

            Seat seat = Seat.builder()
                    .performance(performance)
                    .seatNumber(seatName)
                    .build();
            seatRepository.save(seat);
        }
    }

    /**
     * 공연 좌석 수 변경 시 좌석 업데이트
     */
    private void updateSeatsForPerformance(Performance performance) {
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
            int seatsPerRow = 10;
            int startNumber = currentSeatCount + 1;

            for (int i = 0; i < seatsToAdd; i++) {
                int seatIndex = startNumber + i;
                char row = (char) ('A' + (seatIndex - 1) / seatsPerRow);
                int seatNumber = ((seatIndex - 1) % seatsPerRow) + 1;
                String seatName = row + String.valueOf(seatNumber);

                Seat seat = Seat.builder()
                        .performance(performance)
                        .seatNumber(seatName)
                        .build();
                seatRepository.save(seat);
            }
        }
    }
}
