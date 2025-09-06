package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_seat_performance_number", columnNames = {"performance_id", "seat_number"})
    },
    indexes = {
        @Index(name = "idx_seat_performance", columnList = "performance_id"),
        @Index(name = "idx_seat_number", columnList = "seat_number"),
        @Index(name = "idx_seat_booked", columnList = "is_booked"),
        @Index(name = "idx_seat_performance_booked", columnList = "performance_id, is_booked"),
        @Index(name = "idx_seat_position", columnList = "row_number, seat_in_row"),
        @Index(name = "idx_seat_layout", columnList = "seat_layout_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"performance", "booking"})
@EqualsAndHashCode(of = "id")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "공연 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_seat_performance"))
    private Performance performance;

    @NotBlank(message = "좌석 번호는 필수입니다")
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "seat_in_row")
    private Integer seatInRow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_layout_id", foreignKey = @ForeignKey(name = "fk_seat_layout"))
    private SeatLayout seatLayout;

    @Column(name = "is_booked", nullable = false)
    @Builder.Default
    private Boolean isBooked = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "seat", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Booking booking;

    // 비즈니스 로직 메소드들
    
    /**
     * 좌석 예매 처리
     */
    public void book() {
        if (this.isBooked) {
            throw new IllegalStateException("이미 예매된 좌석입니다: " + this.seatNumber);
        }
        this.isBooked = true;
    }

    /**
     * 좌석 예매 취소 처리
     */
    public void cancel() {
        if (!this.isBooked) {
            throw new IllegalStateException("예매되지 않은 좌석입니다: " + this.seatNumber);
        }
        this.isBooked = false;
    }
    
    /**
     * 좌석이 예매 가능한지 확인
     */
    public boolean isAvailable() {
        return !this.isBooked;
    }
    
    /**
     * 좌석의 행 정보 추출 (A1 -> A)
     */
    public String getRowInfo() {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return "";
        }
        return seatNumber.replaceAll("\\d", "");
    }
    
    /**
     * 좌석의 번호 정보 추출 (A1 -> 1)
     */
    public String getNumberInfo() {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return "";
        }
        return seatNumber.replaceAll("[A-Za-z]", "");
    }
    
    /**
     * 좌석 상태 문자열 반환
     */
    public String getStatusText() {
        return isBooked ? "예매완료" : "예매가능";
    }
}
