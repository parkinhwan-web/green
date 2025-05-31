package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor   // ✅ builder 사용 시 필수
@Builder              // ✅ builder 패턴 적용
public class UserLoginResponse {

    private String message;
    private String email;
    private String username;
    private Long userId;
    private String token;
    private int expiresIn;
}
