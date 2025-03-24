package com.example.demo.controller;

import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserLoginResponse;
import com.example.demo.dto.UserSignupRequest;
import com.example.demo.dto.UserSignupResponse;
import com.example.demo.entity.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.dto.UserUpdateResponse;


import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://green-87zt.onrender.com")
 // ✅ CORS 허용
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * ✅ 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest request) {
        try {
            UserSignupResponse response = userService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * ✅ 로그인 API (JWT 발급)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            UserLoginResponse response = userService.login(request);
            String role = "ROLE_USER"; // ✅ 기본 역할 설정 (추후 DB에서 가져오도록 변경 가능)
            String token = jwtService.generateToken(response.getEmail(), role); // ✅ JWT 토큰 생성

            UserLoginResponse loginResponse = UserLoginResponse.builder()
                .message("로그인 성공!")
                .email(response.getEmail())
                .token(token)
                .expiresIn(3600) // 1시간
                .build();

            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * ✅ 로그아웃 API (JWT 블랙리스트 추가)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"잘못된 토큰입니다.\"}");
        }

        String jwt = token.substring(7);
        if (!jwtService.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"message\": \"유효하지 않은 토큰입니다.\"}");
        }

        jwtService.invalidateToken(jwt); // ✅ JWT 블랙리스트 추가

        return ResponseEntity.ok().body("{\"message\": \"로그아웃 성공!\"}");
    }

    /**
     * ✅ 사용자 정보 조회 API
     */
    @GetMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()") // ✅ 로그인된 사용자만 접근 가능
    public ResponseEntity<?> getUserById(@PathVariable Long user_id) {
        Optional<User> user = userService.getUserById(user_id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"사용자를 찾을 수 없습니다.\"}");
        }

        return ResponseEntity.ok(user.get()); // ✅ 200 OK, 사용자 정보 반환
    }

        /**
     * ✅ 사용자 정보 수정 API
     */
    @PutMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(
            @PathVariable Long user_id,
            @RequestBody UserUpdateRequest request
    ) {
        try {
            UserUpdateResponse response = userService.updateUser(user_id, request);
            return ResponseEntity.ok(response); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{user_id}")
    @PreAuthorize("isAuthenticated()") // 로그인된 사용자만 가능
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().body("{\"message\": \"계정 삭제 성공!\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}
