package com.springproject.stbookingsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTO {

    /**
     * 로그인 요청 DTO
     */
    public static class LoginRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;

        // 기본 생성자
        public LoginRequest() {}

        // 생성자
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 회원가입 요청 DTO
     */
    public static class RegisterRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
        private String password;

        @NotBlank(message = "이름은 필수입니다")
        private String name;

        @NotBlank(message = "전화번호는 필수입니다")
        private String phone;

        private String role = "ROLE_USER";

        // 기본 생성자
        public RegisterRequest() {}

        // 생성자
        public RegisterRequest(String email, String password, String name, String phone) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phone = phone;
        }

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    /**
     * 인증 응답 DTO
     */
    public static class AuthResponse {
        private String token;
        private String role;
        private String name;
        private String email;

        // 기본 생성자
        public AuthResponse() {}

        // 생성자
        public AuthResponse(String token, String role, String name, String email) {
            this.token = token;
            this.role = role;
            this.name = name;
            this.email = email;
        }

        // Getters and Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}