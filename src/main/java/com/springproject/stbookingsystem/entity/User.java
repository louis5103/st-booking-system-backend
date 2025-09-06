package com.springproject.stbookingsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", 
    indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_phone", columnList = "phone", unique = true),
        @Index(name = "idx_user_role", columnList = "role"),
        @Index(name = "idx_user_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password", "bookings"})
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank(message = "전화번호는 필수입니다")
    @Column(name = "phone", nullable = false, length = 20, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // 사용자 역할 열거형
    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }
    
    // 비즈니스 로직 메소드
    public boolean isAdmin() {
        return Role.ROLE_ADMIN.equals(this.role);
    }
    
    public int getBookingCount() {
        return bookings != null ? bookings.size() : 0;
    }
}
