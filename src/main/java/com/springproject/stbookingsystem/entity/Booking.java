package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings",
    indexes = {
        @Index(name = "idx_booking_user", columnList = "user_id"),
        @Index(name = "idx_booking_performance", columnList = "performance_id"),
        @Index(name = "idx_booking_seat", columnList = "seat_id", unique = true),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_date", columnList = "booking_date"),
        @Index(name = "idx_booking_user_performance", columnList = "user_id, performance_id"),
        @Index(name = "idx_booking_user_status", columnList = "user_id, status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "performance", "seat"})
@EqualsAndHashCode(of = "id")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "사용자 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_booking_user"))
    private User user;

    @NotNull(message = "공연 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_booking_performance"))
    private Performance performance;

    @NotNull(message = "좌석 정보는 필수입니다")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_booking_seat"))
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "booking_date", nullable = false)
    @Builder.Default
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 비즈니스 로직 메소드들
    
    /**
     * 예매 취소 처리
     */
    public void cancel() {
        cancel(null);
    }
    
    /**
     * 예매 취소 처리 (사유 포함)
     */
    public void cancel(String reason) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다");
        }
        
        this.status = BookingStatus.CANCELLED;
        this.cancelledDate = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    /**
     * 취소 가능 여부 확인 (공연 24시간 전까지)
     */
    public boolean canCancel() {
        if (status != BookingStatus.CONFIRMED) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime performanceDate = performance.getPerformanceDate();

        // 공연 시작 24시간 전까지만 취소 가능
        return now.isBefore(performanceDate.minusHours(24));
    }
    
    /**
     * 예매 확정 상태인지 확인
     */
    public boolean isConfirmed() {
        return BookingStatus.CONFIRMED.equals(this.status);
    }
    
    /**
     * 예매 취소 상태인지 확인
     */
    public boolean isCancelled() {
        return BookingStatus.CANCELLED.equals(this.status);
    }
    
    /**
     * 예매 가격 반환
     */
    public Integer getPrice() {
        return performance.getPrice();
    }
    
    /**
     * 취소까지 남은 시간 계산 (시간 단위)
     */
    public Long getHoursUntilCancellationDeadline() {
        if (!isConfirmed()) {
            return 0L;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = performance.getPerformanceDate().minusHours(24);
        
        if (now.isAfter(deadline)) {
            return 0L;
        }
        
        return java.time.Duration.between(now, deadline).toHours();
    }
    
    /**
     * 예매 상태 텍스트 반환
     */
    public String getStatusText() {
        return switch (status) {
            case CONFIRMED -> "예매확정";
            case CANCELLED -> "예매취소";
        };
    }

    // 예매 상태 열거형
    public enum BookingStatus {
        CONFIRMED("예매확정"),
        CANCELLED("예매취소");
        
        private final String description;
        
        BookingStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
