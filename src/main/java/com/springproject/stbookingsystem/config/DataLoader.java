package com.springproject.stbookingsystem.config;

import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.entity.SeatLayout;
import com.springproject.stbookingsystem.entity.User;
import com.springproject.stbookingsystem.entity.Venue;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatLayoutRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
import com.springproject.stbookingsystem.repository.UserRepository;
import com.springproject.stbookingsystem.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final SeatLayoutRepository seatLayoutRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Docker 환경에서는 매번 초기 데이터 생성
        // 기존 데이터 확인을 더 관대하게 설정
        try {
            if (userRepository.count() > 0) {
                log.info("초기 데이터가 이미 존재합니다. 스킵합니다.");
                return;
            }
        } catch (Exception e) {
            log.warn("데이터베이스 상태 확인 중 오류 발생, 초기 데이터를 생성합니다: {}", e.getMessage());
        }

        log.info("초기 데이터를 생성합니다...");

        try {
            // 관리자 계정 생성
            createAdminUser();

            // 테스트 사용자 계정 생성
            createTestUsers();

            // 공연장 생성
            createSampleVenues();

            // 샘플 공연 생성
            createSamplePerformances();

            log.info("초기 데이터 생성이 완료되었습니다!");

            // 로그인 정보 출력
            printLoginInfo();
        } catch (Exception e) {
            log.error("초기 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
            // Docker 환경에서는 오류가 발생해도 애플리케이션이 계속 실행되도록 함
        }
    }

    private void createAdminUser() {
        User admin = User.builder()
                .email("admin@st-booking.com")
                .password(passwordEncoder.encode("admin123"))
                .name("관리자")
                .phone("010-0000-0000")
                .role(User.Role.ROLE_ADMIN)
                .build();
        
        userRepository.save(admin);
        log.info("관리자 계정이 생성되었습니다.");
    }

    private void createTestUsers() {
        User user1 = User.builder()
                .email("user1@test.com")
                .password(passwordEncoder.encode("user123"))
                .name("김사용자")
                .phone("010-1111-1111")
                .role(User.Role.ROLE_USER)
                .build();

        User user2 = User.builder()
                .email("user2@test.com")
                .password(passwordEncoder.encode("user123"))
                .name("이고객")
                .phone("010-2222-2222")
                .role(User.Role.ROLE_USER)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        log.info("테스트 사용자 계정이 생성되었습니다.");
    }

    private void createSampleVenues() {
        log.info("샘플 공연장 생성 중...");

        // 세종문화회관 대극장
        Venue sejongTheater = Venue.builder()
                .name("세종문화회관 대극장")
                .location("서울특별시 종로구 세종대로 175")
                .description("국내 최고의 공연장 중 하나로, 다양한 클래식 음악회와 오페라가 열립니다.")
                .totalSeats(3000)
                .totalRows(30)
                .seatsPerRow(100)
                .facilities("주차장, 레스토랑, VIP라운지")
                .contactInfo("02-399-1000")
                .build();
        venueRepository.save(sejongTheater);

        // 블루스퀘어 삼성전자홀
        Venue blueSquare = Venue.builder()
                .name("블루스퀘어 삼성전자홀")
                .location("서울특별시 용산구 이태원로 294")
                .description("뮤지컬과 연극 공연의 메카로 불리는 현대적인 공연장입니다.")
                .totalSeats(1800)
                .totalRows(20)
                .seatsPerRow(90)
                .facilities("주차장, 카페, 기념품샵")
                .contactInfo("1588-5212")
                .build();
        venueRepository.save(blueSquare);

        // 예술의전당 콘서트홀
        Venue sacConcertHall = Venue.builder()
                .name("예술의전당 콘서트홀")
                .location("서울특별시 서초구 남부순환로 2406")
                .description("최고 수준의 음향시설을 갖춘 클래식 전용 공연장입니다.")
                .totalSeats(2600)
                .totalRows(26)
                .seatsPerRow(100)
                .facilities("주차장, 레스토랑, 아트샵")
                .contactInfo("02-580-1300")
                .build();
        venueRepository.save(sacConcertHall);

        // 각 공연장에 기본 좌석 배치 생성
        createSampleSeatLayouts(sejongTheater);
        createSampleSeatLayouts(blueSquare);
        createSampleSeatLayouts(sacConcertHall);

        log.info("샘플 공연장 생성 완료");
    }

    private void createSampleSeatLayouts(Venue venue) {
        log.info("공연장 '{}' 좌석 배치 생성 중...", venue.getName());

        for (int row = 1; row <= Math.min(venue.getTotalRows(), 10); row++) { // 처음 10행만 생성
            for (int seat = 1; seat <= Math.min(venue.getSeatsPerRow(), 20); seat++) { // 행당 20석만 생성
                SeatLayout.SeatType seatType;
                
                // 좌석 타입 결정
                if (row <= 3) {
                    seatType = SeatLayout.SeatType.VIP; // 앞 3행은 VIP
                } else if (row <= 6) {
                    seatType = SeatLayout.SeatType.PREMIUM; // 4-6행은 프리미엄
                } else {
                    seatType = SeatLayout.SeatType.REGULAR; // 나머지는 일반
                }

                // 통로 처리 (중간에 통로 생성)
                if (seat == 6 || seat == 15) {
                    seatType = SeatLayout.SeatType.AISLE;
                }

                SeatLayout seatLayout = SeatLayout.builder()
                        .venue(venue)
                        .rowNumber(row)
                        .seatNumber(seat)
                        .seatType(seatType)
                        .isActive(true)
                        .xPosition(seat * 40) // UI 좌표
                        .yPosition(row * 50)
                        .build();

                seatLayout.generateSeatLabel();
                seatLayoutRepository.save(seatLayout);
            }
        }
    }

    private void createSamplePerformances() {
        log.info("샘플 공연 생성 중...");

        List<Venue> venues = venueRepository.findAll();
        if (venues.isEmpty()) {
            log.warn("공연장이 없어 샘플 공연을 생성할 수 없습니다");
            return;
        }

        Venue venue1 = venues.get(0);
        Venue venue2 = venues.size() > 1 ? venues.get(1) : venue1;
        Venue venue3 = venues.size() > 2 ? venues.get(2) : venue1;

        Performance performance1 = Performance.builder()
                .title("베토벤 교향곡 9번 '합창'")
                .venue(venue1)
                .venueName(venue1.getName())
                .performanceDate(LocalDateTime.now().plusDays(30))
                .price(80000)
                .totalSeats(200) // 실제 생성된 좌석 수에 맞춤
                .description("서울시립교향악단과 함께하는 베토벤의 대표작")
                .imageUrl("/images/beethoven9.jpg")
                .build();
        performanceRepository.save(performance1);

        Performance performance2 = Performance.builder()
                .title("뮤지컬 '레미제라블'")
                .venue(venue2)
                .venueName(venue2.getName())
                .performanceDate(LocalDateTime.now().plusDays(45))
                .price(120000)
                .totalSeats(200)
                .description("감동의 뮤지컬 레미제라블 내한공연")
                .imageUrl("/images/lesmiserables.jpg")
                .build();
        performanceRepository.save(performance2);

        Performance performance3 = Performance.builder()
                .title("발레 '백조의 호수'")
                .venue(venue3)
                .venueName(venue3.getName())
                .performanceDate(LocalDateTime.now().plusDays(60))
                .price(90000)
                .totalSeats(200)
                .description("국립발레단의 아름다운 백조의 호수")
                .imageUrl("/images/swanlake.jpg")
                .build();
        performanceRepository.save(performance3);

        // 각 공연에 좌석 생성
        createSeatsForPerformance(performance1);
        createSeatsForPerformance(performance2);
        createSeatsForPerformance(performance3);

        log.info("샘플 공연 생성 완료");
    }

    private void createSeatsForPerformance(Performance performance) {
        Venue venue = performance.getVenue();
        if (venue == null) {
            log.warn("공연 '{}'에 연결된 공연장이 없습니다", performance.getTitle());
            return;
        }

        // 공연장의 좌석 배치 기준으로 좌석 생성
        List<SeatLayout> seatLayouts = seatLayoutRepository.findByVenueAndIsActiveTrueOrderByRowNumberAscSeatNumberAsc(venue);
        
        if (!seatLayouts.isEmpty()) {
            // 공연장의 좌석 배치가 있는 경우
            for (SeatLayout seatLayout : seatLayouts) {
                if (seatLayout.isBookable()) {
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
            // 기본 좌석 구조로 생성
            int totalSeats = performance.getTotalSeats();
            int seatsPerRow = Math.min(venue.getSeatsPerRow(), 20); // 최대 20석

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

    private void printLoginInfo() {
        log.info("\n" +
                "=== 로그인 정보 ===\n" +
                "관리자 계정:\n" +
                "  이메일: admin@st-booking.com\n" +
                "  비밀번호: admin123\n" +
                "\n" +
                "테스트 사용자 계정:\n" +
                "  이메일: user1@test.com\n" +
                "  비밀번호: user123\n" +
                "\n" +
                "  이메일: user2@test.com\n" +
                "  비밀번호: user123\n" +
                "==================\n");
    }
}
