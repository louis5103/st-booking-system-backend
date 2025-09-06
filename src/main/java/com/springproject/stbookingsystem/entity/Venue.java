package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues",
    indexes = {
        @Index(name = "idx_venue_name", columnList = "name"),
        @Index(name = "idx_venue_location", columnList = "location")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"performances", "seatLayouts"})
@EqualsAndHashCode(of = "id")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "공연장명은 필수입니다")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "주소는 필수입니다")
    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "전체 좌석 수는 필수입니다")
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @NotNull(message = "행 수는 필수입니다")
    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @NotNull(message = "열 수는 필수입니다")
    @Column(name = "seats_per_row", nullable = false)
    private Integer seatsPerRow;

    @Column(name = "facilities", length = 500)
    private String facilities; // 편의시설 정보

    @Column(name = "contact_info", length = 100)
    private String contactInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 연관관계
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Performance> performances = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SeatLayout> seatLayouts = new ArrayList<>();

    // 비즈니스 로직 메소드들

    /**
     * 공연장 정보 업데이트
     */
    public void updateInfo(String name, String location, String description, 
                          String facilities, String contactInfo) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (location != null && !location.trim().isEmpty()) {
            this.location = location.trim();
        }
        this.description = description;
        this.facilities = facilities;
        this.contactInfo = contactInfo;
    }

    /**
     * 좌석 구조 업데이트
     */
    public void updateSeatStructure(Integer totalSeats, Integer totalRows, Integer seatsPerRow) {
        if (totalSeats != null && totalSeats > 0) {
            this.totalSeats = totalSeats;
        }
        if (totalRows != null && totalRows > 0) {
            this.totalRows = totalRows;
        }
        if (seatsPerRow != null && seatsPerRow > 0) {
            this.seatsPerRow = seatsPerRow;
        }
    }

    /**
     * 공연이 진행 중인지 확인
     */
    public boolean hasActivePerformances() {
        return performances.stream()
                .anyMatch(performance -> performance.getPerformanceDate().isAfter(LocalDateTime.now()));
    }

    /**
     * 기본 좌석 번호 생성 (A1, A2, B1, B2 형식)
     */
    public String generateSeatNumber(int row, int seatInRow) {
        char rowChar = (char) ('A' + row - 1);
        return rowChar + String.valueOf(seatInRow);
    }

    /**
     * 좌석 번호에서 행 정보 추출
     */
    public int getRowFromSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return -1;
        }
        return seatNumber.charAt(0) - 'A' + 1;
    }

    /**
     * 좌석 번호에서 열 정보 추출
     */
    public int getColumnFromSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.length() < 2) {
            return -1;
        }
        try {
            return Integer.parseInt(seatNumber.substring(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
