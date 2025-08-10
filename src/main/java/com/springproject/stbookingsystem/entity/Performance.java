package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "performances")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "공연명은 필수입니다")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "공연장은 필수입니다")
    @Column(nullable = false)
    private String venue;

    @NotNull(message = "공연 일시는 필수입니다")
    @Column(name = "performance_date", nullable = false)
    private LocalDateTime performanceDate;

    @Positive(message = "가격은 0보다 커야 합니다")
    @Column(nullable = false)
    private Integer price;

    @Positive(message = "총 좌석 수는 0보다 커야 합니다")
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // 기본 생성자
    public Performance() {}

    // 생성자
    public Performance(String title, String venue, LocalDateTime performanceDate, Integer price, Integer totalSeats) {
        this.title = title;
        this.venue = venue;
        this.performanceDate = performanceDate;
        this.price = price;
        this.totalSeats = totalSeats;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 예매된 좌석 수 계산
    public Integer getBookedSeats() {
        return (int) seats.stream().filter(Seat::getIsBooked).count();
    }

    // 잔여 좌석 수 계산
    public Integer getAvailableSeats() {
        return totalSeats - getBookedSeats();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(LocalDateTime performanceDate) {
        this.performanceDate = performanceDate;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
