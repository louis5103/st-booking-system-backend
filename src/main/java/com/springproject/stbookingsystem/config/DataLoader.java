package com.springproject.stbookingsystem.config;


import com.springproject.stbookingsystem.entity.Performance;
import com.springproject.stbookingsystem.entity.Seat;
import com.springproject.stbookingsystem.entity.User;
import com.springproject.stbookingsystem.repository.PerformanceRepository;
import com.springproject.stbookingsystem.repository.SeatRepository;
import com.springproject.stbookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 기존 데이터가 있는지 확인
        if (userRepository.count() > 0) {
            System.out.println("초기 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        System.out.println("초기 데이터를 생성합니다...");

        // 관리자 계정 생성
        createAdminUser();

        // 테스트 사용자 계정 생성
        createTestUsers();

        // 샘플 공연 생성
        createSamplePerformances();

        System.out.println("초기 데이터 생성이 완료되었습니다!");

        // 로그인 정보 출력
        printLoginInfo();
    }

    private void createAdminUser() {
        User admin = new User(
                "admin@st-booking.com",
                passwordEncoder.encode("admin123"),
                "관리자",
                "010-0000-0000",
                User.Role.ROLE_ADMIN
        );
        userRepository.save(admin);
        System.out.println("관리자 계정이 생성되었습니다.");
    }

    private void createTestUsers() {
        User user1 = new User(
                "user1@test.com",
                passwordEncoder.encode("user123"),
                "김사용자",
                "010-1111-1111",
                User.Role.ROLE_USER
        );

        User user2 = new User(
                "user2@test.com",
                passwordEncoder.encode("user123"),
                "이고객",
                "010-2222-2222",
                User.Role.ROLE_USER
        );

        userRepository.save(user1);
        userRepository.save(user2);
        System.out.println("테스트 사용자 계정이 생성되었습니다.");
    }

    private void createSamplePerformances() {
        // 뮤지컬 공연 - Unsplash에서 제공하는 실제 뮤지컬 관련 이미지
        Performance musical = new Performance(
                "레미제라블",
                "예술의전당 오페라극장",
                LocalDateTime.now().plusDays(30),
                150000,
                100
        );
        musical.setDescription("빅토르 위고의 대작을 바탕으로 한 감동적인 뮤지컬");
        musical.setImageUrl("https://images.unsplash.com/photo-1507924538820-ede94a04019d?w=400&h=600&fit=crop");
        performanceRepository.save(musical);
        createSeatsForPerformance(musical);

        // 콘서트 공연 - 실제 콘서트 무대 이미지
        Performance concert = new Performance(
                "BTS 월드 투어",
                "잠실 올림픽 주경기장",
                LocalDateTime.now().plusDays(45),
                250000,
                200
        );
        concert.setDescription("전 세계를 뜨겁게 달군 BTS의 콘서트");
        concert.setImageUrl("https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=600&fit=crop");
        performanceRepository.save(concert);
        createSeatsForPerformance(concert);

        // 연극 공연 - 클래식한 극장 이미지
        Performance play = new Performance(
                "햄릿",
                "국립극장 해오름극장",
                LocalDateTime.now().plusDays(20),
                80000,
                80
        );
        play.setDescription("셰익스피어의 불멸의 작품 햄릿");
        play.setImageUrl("https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?w=400&h=600&fit=crop");
        performanceRepository.save(play);
        createSeatsForPerformance(play);

        // 클래식 공연 - 오케스트라 연주 이미지
        Performance classic = new Performance(
                "베토벤 교향곡 9번",
                "세종문화회관 대극장",
                LocalDateTime.now().plusDays(60),
                120000,
                150
        );
        classic.setDescription("서울시향과 함께하는 베토벤의 합창 교향곡");
        classic.setImageUrl("https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=600&fit=crop");
        performanceRepository.save(classic);
        createSeatsForPerformance(classic);

        System.out.println("샘플 공연 데이터가 생성되었습니다.");
    }

    private void createSeatsForPerformance(Performance performance) {
        int totalSeats = performance.getTotalSeats();
        int seatsPerRow = 10; // 한 행당 10석

        for (int i = 1; i <= totalSeats; i++) {
            char row = (char) ('A' + (i - 1) / seatsPerRow);
            int seatNumber = ((i - 1) % seatsPerRow) + 1;
            String seatName = row + String.valueOf(seatNumber);

            Seat seat = new Seat(performance, seatName);
            seatRepository.save(seat);
        }
    }

    private void printLoginInfo() {
        System.out.println("\n" +
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