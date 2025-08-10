package com.springproject.stbookingsystem.controller;

import com.springproject.stbookingsystem.dto.AuthDTO;
import com.springproject.stbookingsystem.sevice.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO.LoginRequest loginRequest) {
        try {
            AuthDTO.AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("로그인 실패: " + e.getMessage()));
        }
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO.RegisterRequest registerRequest) {
        try {
            AuthDTO.AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("회원가입 실패: " + e.getMessage()));
        }
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = authService.isEmailExists(email);
        return ResponseEntity.ok(new CheckResponse("email", exists));
    }

    /**
     * 전화번호 중복 확인
     */
    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhone(@RequestParam String phone) {
        boolean exists = authService.isPhoneExists(phone);
        return ResponseEntity.ok(new CheckResponse("phone", exists));
    }

    /**
     * 현재 로그인한 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            var user = authService.getCurrentUser();
            return ResponseEntity.ok(new UserInfo(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("사용자 정보 조회 실패: " + e.getMessage()));
        }
    }

    // 응답 클래스들
    static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    static class CheckResponse {
        private String field;
        private boolean exists;

        public CheckResponse(String field, boolean exists) {
            this.field = field;
            this.exists = exists;
        }

        public String getField() {
            return field;
        }

        public boolean isExists() {
            return exists;
        }
    }

    static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String role;

        public UserInfo(Long id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }
    }
}