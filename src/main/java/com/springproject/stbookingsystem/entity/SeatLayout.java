package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_layouts",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_seat_layout_venue_position", 
                         columnNames = {"venue_id", "seat_row", "seat_col"})
    },
    indexes = {
        @Index(name = "idx_seat_layout_venue", columnList = "venue_id"),
        @Index(name = "idx_seat_layout_position", columnList = "seat_row, seat_col"),
        @Index(name = "idx_seat_layout_type", columnList = "seat_type"),
        @Index(name = "idx_seat_layout_status", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"venue"})
@EqualsAndHashCode(of = "id")
public class SeatLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "공연장 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_seat_layout_venue"))
    private Venue venue;

    @NotNull(message = "행 번호는 필수입니다")
    @Column(name = "seat_row", nullable = false)
    private Integer rowNumber;

    @NotNull(message = "좌석 번호는 필수입니다")
    @Column(name = "seat_col", nullable = false)
    private Integer seatNumber;

    @NotBlank(message = "좌석 표시명은 필수입니다")
    @Column(name = "seat_label", nullable = false, length = 10)
    private String seatLabel; // A1, B2 등

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 20)
    @Builder.Default
    private SeatType seatType = SeatType.REGULAR;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "x_position")
    private Integer xPosition; // UI에서의 X 좌표

    @Column(name = "y_position")
    private Integer yPosition; // UI에서의 Y 좌표

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 좌석 타입 열거형
    public enum SeatType {
        REGULAR("일반석"),
        VIP("VIP석"),
        PREMIUM("프리미엄석"),
        WHEELCHAIR("휠체어석"),
        BLOCKED("차단된 좌석"),
        AISLE("통로"),
        STAGE("무대");

        private final String description;

        SeatType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 비즈니스 로직 메소드들

    /**
     * 좌석 정보 업데이트
     */
    public void updateSeatInfo(SeatType seatType, Boolean isActive, String description) {
        if (seatType != null) {
            this.seatType = seatType;
        }
        if (isActive != null) {
            this.isActive = isActive;
        }
        this.description = description;
    }

    /**
     * 좌석 위치 업데이트
     */
    public void updatePosition(Integer xPosition, Integer yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    /**
     * 좌석이 예매 가능한지 확인
     */
    public boolean isBookable() {
        return isActive && (seatType == SeatType.REGULAR || 
                           seatType == SeatType.VIP || 
                           seatType == SeatType.PREMIUM ||
                           seatType == SeatType.WHEELCHAIR);
    }

    /**
     * 좌석이 물리적으로 존재하는지 확인
     */
    public boolean isPhysicalSeat() {
        return seatType != SeatType.AISLE && 
               seatType != SeatType.STAGE && 
               seatType != SeatType.BLOCKED;
    }

    /**
     * 좌석 표시명 자동 생성
     */
    public void generateSeatLabel() {
        if (venue != null && rowNumber != null && seatNumber != null) {
            this.seatLabel = venue.generateSeatNumber(rowNumber, seatNumber);
        }
    }
}
