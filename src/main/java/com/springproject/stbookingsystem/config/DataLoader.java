package com.springproject.stbookingsystem.config;

import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.entity.User;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
import com.springproject.stbookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 기존 데이터가 있는지 확인
        if (userRepository.count() > 0) {
            log.info("초기 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("초기 데이터를 생성합니다...");

        // 관리자 계정 생성
        createAdminUser();

        // 테스트 사용자 계정 생성
        createTestUsers();

        // 샘플 공연 생성
        createSamplePerformances();

        log.info("초기 데이터 생성이 완료되었습니다!");

        // 로그인 정보 출력
        printLoginInfo();
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

    private void createSamplePerformances() {
        // 뮤지컬 공연
        Performance musical = Performance.builder()
                .title("레미제라블")
                .venue("예술의전당 오페라극장")
                .performanceDate(LocalDateTime.now().plusDays(30))
                .price(150000)
                .totalSeats(100)
                .description("빅토르 위고의 대작을 바탕으로 한 감동적인 뮤지컬")
                .imageUrl("https://images.unsplash.com/photo-1507924538820-ede94a04019d?w=400&h=600&fit=crop")
                .build();
        
        performanceRepository.save(musical);
        createSeatsForPerformance(musical);

        // 콘서트 공연
        Performance concert = Performance.builder()
                .title("BTS 월드 투어")
                .venue("잠실 올림픽 주경기장")
                .performanceDate(LocalDateTime.now().plusDays(45))
                .price(250000)
                .totalSeats(200)
                .description("전 세계를 뜨겁게 달군 BTS의 콘서트")
                .imageUrl("https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=600&fit=crop")
                .build();
        
        performanceRepository.save(concert);
        createSeatsForPerformance(concert);

        // 연극 공연
        Performance play = Performance.builder()
                .title("햄릿")
                .venue("국립극장 해오름극장")
                .performanceDate(LocalDateTime.now().plusDays(20))
                .price(80000)
                .totalSeats(80)
                .description("셰익스피어의 불멸의 작품 햄릿")
                .imageUrl("https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?w=400&h=600&fit=crop")
                .build();
        
        performanceRepository.save(play);
        createSeatsForPerformance(play);

        // 클래식 공연
        Performance classic = Performance.builder()
                .title("베토벤 교향곡 9번")
                .venue("세종문화회관 대극장")
                .performanceDate(LocalDateTime.now().plusDays(60))
                .price(120000)
                .totalSeats(150)
                .description("서울시향과 함께하는 베토벤의 합창 교향곡")
                .imageUrl("https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=600&fit=crop")
                .build();
        
        performanceRepository.save(classic);
        createSeatsForPerformance(classic);

        log.info("샘플 공연 데이터가 생성되었습니다.");
    }

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
