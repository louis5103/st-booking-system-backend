package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "performances",
    indexes = {
        @Index(name = "idx_performance_date", columnList = "performance_date"),
        @Index(name = "idx_performance_title", columnList = "title"),
        @Index(name = "idx_performance_venue", columnList = "venue"),
        @Index(name = "idx_performance_price", columnList = "price"),
        @Index(name = "idx_performance_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"seats", "bookings"})
@EqualsAndHashCode(of = "id")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "공연명은 필수입니다")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "공연장은 필수입니다")
    @Column(name = "venue", nullable = false, length = 100)
    private String venue;

    @NotNull(message = "공연 일시는 필수입니다")
    @Column(name = "performance_date", nullable = false)
    private LocalDateTime performanceDate;

    @Positive(message = "가격은 0보다 커야 합니다")
    @Column(name = "price", nullable = false)
    private Integer price;

    @Positive(message = "총 좌석 수는 0보다 커야 합니다")
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // 비즈니스 로직 메소드들
    
    /**
     * 예매된 좌석 수 계산
     */
    public Integer getBookedSeats() {
        return (int) seats.stream().filter(seat -> seat.getIsBooked()).count();
    }

    /**
     * 잔여 좌석 수 계산
     */
    public Integer getAvailableSeats() {
        return totalSeats - getBookedSeats();
    }
    
    /**
     * 예매율 계산 (퍼센티지)
     */
    public Double getBookingRate() {
        if (totalSeats == 0) return 0.0;
        return (double) getBookedSeats() / totalSeats * 100;
    }
    
    /**
     * 공연이 매진되었는지 확인
     */
    public boolean isSoldOut() {
        return getAvailableSeats() <= 0;
    }
    
    /**
     * 공연이 이미 지났는지 확인
     */
    public boolean isPast() {
        return performanceDate.isBefore(LocalDateTime.now());
    }
    
    /**
     * 예매 가능한 공연인지 확인
     */
    public boolean isBookable() {
        return !isPast() && !isSoldOut();
    }
    
    /**
     * 총 예상 수익 계산
     */
    public Long getTotalRevenue() {
        return (long) getBookedSeats() * price;
    }
    
    /**
     * 최대 수익 계산
     */
    public Long getMaxRevenue() {
        return (long) totalSeats * price;
    }
}
