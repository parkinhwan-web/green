package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor  // 기본 생성자 필요
@AllArgsConstructor // 모든 필드 포함한 생성자 필요
public class UserLoginResponse {
    private String message;
    private String email;  
    private String username;
    private String token;
    private int expiresIn;
}
