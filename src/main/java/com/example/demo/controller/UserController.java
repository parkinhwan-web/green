package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Point;
import com.example.demo.entity.User;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://green-87zt.onrender.com") // ✅ CORS 허용
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RecycleService recycleService;
    private final PointHistoryService pointHistoryService;
    private final UserCouponService userCouponService;

    /** 회원가입 API */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest request) {
        try {
            UserSignupResponse response = userService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /** ✅ 수정된 로그인 API (UserService 반환값 그대로 사용) */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            UserLoginResponse response = userService.login(request);
            return ResponseEntity.ok(response); // ✅ 그대로 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /** 로그아웃 API */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\": \"잘못된 토큰입니다.\"}");
        }

        String jwt = token.substring(7);
        if (!jwtService.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\": \"유효하지 않은 토큰입니다.\"}");
        }

        jwtService.invalidateToken(jwt);
        return ResponseEntity.ok().body("{\"message\": \"로그아웃 성공!\"}");
    }

    /** 사용자 정보 조회 API */
    @GetMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserById(@PathVariable Long user_id) {
        Optional<User> user = userService.getUserById(user_id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"사용자를 찾을 수 없습니다.\"}");
        }
        return ResponseEntity.ok(user.get());
    }

    /** 사용자 정보 수정 API */
    @PutMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@PathVariable Long user_id, @RequestBody UserUpdateRequest request) {
        try {
            UserUpdateResponse response = userService.updateUser(user_id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /** 계정 삭제 API */
    @DeleteMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().body("{\"message\": \"계정 삭제 성공!\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /** 프로필 조회 API */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\": \"JWT 토큰이 필요합니다.\"}");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        Optional<User> user = userService.getProfile(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"사용자를 찾을 수 없습니다.\"}");
        }

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.get().getId())
                .username(user.get().getUsername())
                .email(user.get().getEmail())
                .build();

        return ResponseEntity.ok(response);
    }

    /** 포인트 조회 API */
    @GetMapping("/{user_id}/points")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserPoints(@PathVariable("user_id") Long userId) {
        try {
            Point point = recycleService.getUserPointInfo(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("user_id", point.getUserId());
            response.put("points", point.getPoints());
            response.put("last_updated", point.getUpdatedAt().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /** 포인트 사용 API */
    @PostMapping("/{user_id}/points/use")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> usePoints(
            @PathVariable("user_id") Long userId,
            @RequestBody PointUsageRequest request) {
        try {
            PointUsageResponse response = userService.usePoints(userId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /** 앱 설정 조회 API */
    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAppSettings(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "JWT 토큰이 필요합니다."));
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        Long userId = userService.getUserIdByEmail(email);

        try {
            AppSettingsResponse response = userService.getAppSettings(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /** 포인트 내역 조회 API */
    @GetMapping("/{userId}/points/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PointHistoryResponse>> getPointHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<PointHistoryResponse> history = pointHistoryService.getHistory(userId);
        return ResponseEntity.ok(history);
    }

    /** 쿠폰함 조회 API */
    @GetMapping("/{userId}/coupons")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserCouponBoxResponse> getUserCoupons(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserCouponBoxResponse response = userCouponService.getUserCoupons(userId);
        return ResponseEntity.ok(response);
    }
}
