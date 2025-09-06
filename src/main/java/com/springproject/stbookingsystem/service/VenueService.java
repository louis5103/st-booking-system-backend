package com.springproject.stbookingsystem.service;

import com.springproject.stbookingsystem.dto.VenueDTO;
import com.springproject.stbookingsystem.entity.Venue;
import com.springproject.stbookingsystem.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;

    /**
     * 모든 공연장 조회
     */
    public List<VenueDTO.VenueResponse> getAllVenues() {
        log.info("모든 공연장 조회 요청");
        return venueRepository.findAllByOrderByNameAsc()
                .stream()
                .map(VenueDTO.VenueResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 공연장 ID로 조회
     */
    public VenueDTO.VenueResponse getVenueById(Long id) {
        log.info("공연장 조회 요청: ID = {}", id);
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + id));
        return VenueDTO.VenueResponse.from(venue);
    }

    /**
     * 공연장 등록
     */
    @Transactional
    public VenueDTO.VenueResponse createVenue(VenueDTO.VenueRequest request) {
        log.info("공연장 등록 요청: {}", request.getName());

        // 공연장명 중복 확인
        if (venueRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 공연장명입니다: " + request.getName());
        }

        // 좌석 구조 검증
        validateSeatStructure(request.getTotalSeats(), request.getTotalRows(), request.getSeatsPerRow());

        Venue venue = Venue.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .totalSeats(request.getTotalSeats())
                .totalRows(request.getTotalRows())
                .seatsPerRow(request.getSeatsPerRow())
                .facilities(request.getFacilities())
                .contactInfo(request.getContactInfo())
                .build();

        Venue savedVenue = venueRepository.save(venue);
        log.info("공연장 등록 완료: ID = {}, 이름 = {}", savedVenue.getId(), savedVenue.getName());

        return VenueDTO.VenueResponse.from(savedVenue);
    }

    /**
     * 공연장 정보 수정
     */
    @Transactional
    public VenueDTO.VenueResponse updateVenue(Long id, VenueDTO.VenueRequest request) {
        log.info("공연장 수정 요청: ID = {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + id));

        // 공연장명 중복 확인 (자기 자신 제외)
        if (!venue.getName().equals(request.getName()) && 
            venueRepository.existsByName(request.getName())) {
            throw new RuntimeException("이미 존재하는 공연장명입니다: " + request.getName());
        }

        // 활성 공연이 있는 경우 좌석 구조 변경 제한
        if (venue.hasActivePerformances()) {
            if (!venue.getTotalSeats().equals(request.getTotalSeats()) ||
                !venue.getTotalRows().equals(request.getTotalRows()) ||
                !venue.getSeatsPerRow().equals(request.getSeatsPerRow())) {
                throw new RuntimeException("진행 중인 공연이 있어 좌석 구조를 변경할 수 없습니다");
            }
        } else {
            // 좌석 구조 검증
            validateSeatStructure(request.getTotalSeats(), request.getTotalRows(), request.getSeatsPerRow());
            venue.updateSeatStructure(request.getTotalSeats(), request.getTotalRows(), request.getSeatsPerRow());
        }

        venue.updateInfo(request.getName(), request.getLocation(), request.getDescription(),
                        request.getFacilities(), request.getContactInfo());

        Venue savedVenue = venueRepository.save(venue);
        log.info("공연장 수정 완료: ID = {}", savedVenue.getId());

        return VenueDTO.VenueResponse.from(savedVenue);
    }

    /**
     * 공연장 좌석 구조만 수정
     */
    @Transactional
    public VenueDTO.VenueResponse updateVenueSeatStructure(Long id, VenueDTO.VenueSeatStructureRequest request) {
        log.info("공연장 좌석 구조 수정 요청: ID = {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + id));

        // 활성 공연이 있는 경우 변경 제한
        if (venue.hasActivePerformances()) {
            throw new RuntimeException("진행 중인 공연이 있어 좌석 구조를 변경할 수 없습니다");
        }

        // 좌석 구조 검증
        validateSeatStructure(request.getTotalSeats(), request.getTotalRows(), request.getSeatsPerRow());

        venue.updateSeatStructure(request.getTotalSeats(), request.getTotalRows(), request.getSeatsPerRow());

        Venue savedVenue = venueRepository.save(venue);
        log.info("공연장 좌석 구조 수정 완료: ID = {}", savedVenue.getId());

        return VenueDTO.VenueResponse.from(savedVenue);
    }

    /**
     * 공연장 삭제
     */
    @Transactional
    public void deleteVenue(Long id) {
        log.info("공연장 삭제 요청: ID = {}", id);

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다: " + id));

        // 활성 공연이 있는 경우 삭제 제한
        if (venue.hasActivePerformances()) {
            throw new RuntimeException("진행 중인 공연이 있어 공연장을 삭제할 수 없습니다");
        }

        venueRepository.delete(venue);
        log.info("공연장 삭제 완료: ID = {}", id);
    }

    /**
     * 공연장 검색
     */
    public List<VenueDTO.VenueSimple> searchVenues(VenueDTO.VenueSearchRequest searchRequest) {
        log.info("공연장 검색 요청: {}", searchRequest);

        List<Venue> venues = venueRepository.findAllByOrderByNameAsc();

        return venues.stream()
                .filter(venue -> matchesSearchCriteria(venue, searchRequest))
                .map(VenueDTO.VenueSimple::from)
                .collect(Collectors.toList());
    }

    /**
     * 활성 공연이 있는 공연장 목록 조회
     */
    public List<VenueDTO.VenueSimple> getVenuesWithActivePerformances() {
        log.info("활성 공연이 있는 공연장 목록 조회");
        return venueRepository.findVenuesWithActivePerformances()
                .stream()
                .map(VenueDTO.VenueSimple::from)
                .collect(Collectors.toList());
    }

    /**
     * 공연장 통계 조회
     */
    public List<VenueDTO.VenueStatistics> getVenueStatistics() {
        log.info("공연장 통계 조회 요청");
        // 추후 구현 - 복잡한 통계 쿼리 필요
        return List.of();
    }

    // 비공개 메소드들

    /**
     * 좌석 구조 검증
     */
    private void validateSeatStructure(Integer totalSeats, Integer totalRows, Integer seatsPerRow) {
        if (totalSeats <= 0) {
            throw new RuntimeException("전체 좌석 수는 0보다 커야 합니다");
        }
        if (totalRows <= 0) {
            throw new RuntimeException("행 수는 0보다 커야 합니다");
        }
        if (seatsPerRow <= 0) {
            throw new RuntimeException("행당 좌석 수는 0보다 커야 합니다");
        }
        if (totalRows * seatsPerRow < totalSeats) {
            throw new RuntimeException("행 수 × 행당 좌석 수가 전체 좌석 수보다 작을 수 없습니다");
        }
        if (totalRows > 26) {
            throw new RuntimeException("행 수는 26개(A~Z)를 초과할 수 없습니다");
        }
        if (seatsPerRow > 50) {
            throw new RuntimeException("행당 좌석 수는 50석을 초과할 수 없습니다");
        }
    }

    /**
     * 검색 조건 매칭
     */
    private boolean matchesSearchCriteria(Venue venue, VenueDTO.VenueSearchRequest searchRequest) {
        if (searchRequest.getName() != null && 
            !venue.getName().toLowerCase().contains(searchRequest.getName().toLowerCase())) {
            return false;
        }
        if (searchRequest.getLocation() != null && 
            !venue.getLocation().toLowerCase().contains(searchRequest.getLocation().toLowerCase())) {
            return false;
        }
        if (searchRequest.getMinSeats() != null && 
            venue.getTotalSeats() < searchRequest.getMinSeats()) {
            return false;
        }
        if (searchRequest.getMaxSeats() != null && 
            venue.getTotalSeats() > searchRequest.getMaxSeats()) {
            return false;
        }
        if (searchRequest.getHasActivePerformances() != null && 
            venue.hasActivePerformances() != searchRequest.getHasActivePerformances()) {
            return false;
        }
        return true;
    }
}
