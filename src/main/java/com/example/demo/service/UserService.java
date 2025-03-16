package com.example.demo.service;

import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserLoginResponse;
import com.example.demo.dto.UserSignupRequest;
import com.example.demo.dto.UserSignupResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // ✅ PasswordEncoder를 Spring 빈으로 주입

    /**
     * ✅ 회원가입 메서드
     */
    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {
        if (request.getEmail() == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("요청 데이터가 잘못되었습니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        // 사용자 엔티티 생성
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ 비밀번호 암호화

        User savedUser = userRepository.save(user);

        return UserSignupResponse.builder()
                .message("회원가입 성공!")
                .userId(savedUser.getId())
                .createdAt(ZonedDateTime.now().toString())
                .build();
    }

    /**
     * ✅ 로그인 메서드 (JWT 발급)
     */
    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        User user = optionalUser.get();
        String token = jwtUtil.generateToken(user.getEmail()); // ✅ JWT 토큰 생성 (email 사용)

        return UserLoginResponse.builder()
                .message("로그인 성공!")
                .email(user.getEmail()) 
                .token(token) // ✅ JWT 토큰 포함
                .expiresIn(3600) // ✅ 토큰 만료 시간 (초)
                .build();
    }

    /**
     * ✅ 사용자 정보 조회 메서드
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
