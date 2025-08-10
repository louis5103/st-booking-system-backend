package com.springproject.stbookingsystem.sevice;


import com.springproject.stbookingsystem.dto.SeatDTO;
import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    /**
     * 특정 공연의 모든 좌석 조회
     */
    public List<SeatDTO.SeatResponse> getSeatsByPerformanceId(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        return seatRepository.findByPerformanceOrderBySeatNumberAsc(performance)
                .stream()
                .map(SeatDTO.SeatResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 공연의 예매 가능한 좌석 조회
     */
    public List<SeatDTO.SeatResponse> getAvailableSeatsByPerformanceId(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        return seatRepository.findByPerformanceAndIsBookedFalseOrderBySeatNumberAsc(performance)
                .stream()
                .map(SeatDTO.SeatResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 공연의 예매된 좌석 조회
     */
    public List<SeatDTO.SeatResponse> getBookedSeatsByPerformanceId(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        return seatRepository.findByPerformanceAndIsBookedTrueOrderBySeatNumberAsc(performance)
                .stream()
                .map(SeatDTO.SeatResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 좌석 ID로 좌석 조회
     */
    public SeatDTO.SeatResponse getSeatById(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다"));

        return SeatDTO.SeatResponse.from(seat);
    }

    /**
     * 좌석 예매 가능 여부 확인
     */
    public boolean isSeatAvailable(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다"));

        return !seat.getIsBooked();
    }

    /**
     * 특정 공연의 좌석 통계 조회
     */
    public SeatStatistics getSeatStatistics(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다"));

        Long totalSeats = (long) performance.getTotalSeats();
        Long bookedSeats = seatRepository.countBookedSeatsByPerformanceId(performanceId);
        Long availableSeats = totalSeats - bookedSeats;

        return new SeatStatistics(totalSeats, bookedSeats, availableSeats);
    }

    /**
     * 좌석 통계를 위한 내부 클래스
     */
    public static class SeatStatistics {
        private Long totalSeats;
        private Long bookedSeats;
        private Long availableSeats;

        public SeatStatistics(Long totalSeats, Long bookedSeats, Long availableSeats) {
            this.totalSeats = totalSeats;
            this.bookedSeats = bookedSeats;
            this.availableSeats = availableSeats;
        }

        // Getters
        public Long getTotalSeats() {
            return totalSeats;
        }

        public Long getBookedSeats() {
            return bookedSeats;
        }

        public Long getAvailableSeats() {
            return availableSeats;
        }

        public double getBookingRate() {
            if (totalSeats == 0) return 0.0;
            return (double) bookedSeats / totalSeats * 100;
        }
    }
}
