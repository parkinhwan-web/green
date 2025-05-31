package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.AppSettings;
import com.example.demo.entity.Point;
import com.example.demo.entity.User;
import com.example.demo.repository.AppSettingsRepository;
import com.example.demo.repository.PointRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final PointRepository pointRepository;
    private final AppSettingsRepository appSettingsRepository;

    /**
     * ✅ 회원가입
     */
    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {
        if (request.getEmail() == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("요청 데이터가 잘못되었습니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return UserSignupResponse.builder()
                .message("회원가입 성공!")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .createdAt(ZonedDateTime.now().toString())
                .build();
    }

    /**
     * ✅ 로그인 (JWT 발급)
     */
    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        User user = optionalUser.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUsername());

        return UserLoginResponse.builder()
                .message("로그인 성공!")
                .email(user.getEmail())
                .username(user.getUsername())
                .userId(user.getId())
                .token(token)
                .expiresIn(3600)
                .build();
    }

    /**
     * ✅ 사용자 정보 조회 (user_id로)
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * ✅ 사용자 정보 수정
     */
    @Transactional
    public UserUpdateResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);

        return new UserUpdateResponse("사용자 정보 수정 성공!");
    }

    /**
     * ✅ 사용자 삭제
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        userRepository.deleteById(userId);
    }

    /**
     * ✅ 사용자 프로필 조회 (이메일 기반, JWT에서 추출)
     */
    @Transactional(readOnly = true)
    public Optional<User> getProfile(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * ✅ 포인트 사용
     */
    public PointUsageResponse usePoints(Long userId, PointUsageRequest request) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));

        if (request.getPointsToUse() > point.getPoints()) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        point.setPoints(point.getPoints() - request.getPointsToUse());
        pointRepository.save(point);

        return PointUsageResponse.builder()
                .message("포인트 사용 성공!")
                .remainingPoints(point.getPoints())
                .updatedAt(point.getUpdatedAt().toString())
                .build();
    }

    /**
     * ✅ 이메일로 사용자 ID 반환
     */
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."))
                .getId();
    }

    /**
     * ✅ 앱 설정 정보 조회
     */
    public AppSettingsResponse getAppSettings(Long userId) {
        AppSettings settings = appSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("앱 설정 정보가 없습니다."));

        return AppSettingsResponse.builder()
                .theme(settings.getTheme())
                .notifications(settings.isNotifications())
                .language(settings.getLanguage())
                .build();
    }
}
