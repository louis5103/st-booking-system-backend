package com.springproject.stbookingsystem.sevice;


import com.springproject.stbookingsystem.dto.AuthDTO;
import com.springproject.stbookingsystem.entity.User;
import com.springproject.stbookingsystem.repository.UserRepository;
import com.springproject.stbookingsystem.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 로그인 처리
     */
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        return new AuthDTO.AuthResponse(
                jwt,
                user.getRole().name(),
                user.getName(),
                user.getEmail()
        );
    }

    /**
     * 회원가입 처리
     */
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest registerRequest) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }

        // 전화번호 중복 확인
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("이미 존재하는 전화번호입니다");
        }

        // 새 사용자 생성
        User user = new User(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getName(),
                registerRequest.getPhone(),
                User.Role.valueOf(registerRequest.getRole())
        );

        User savedUser = userRepository.save(user);

        // JWT 토큰 생성
        String jwt = jwtUtils.generateJwtToken(savedUser.getEmail());

        return new AuthDTO.AuthResponse(
                jwt,
                savedUser.getRole().name(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    /**
     * 이메일 중복 확인
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 전화번호 중복 확인
     */
    public boolean isPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }
}