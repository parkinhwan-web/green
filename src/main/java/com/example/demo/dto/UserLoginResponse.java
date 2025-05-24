package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginResponse {

    private String message;
    private String email;
    private String username;
    private Long userId;
    private String token;
    private int expiresIn;

    @Builder
    public UserLoginResponse(String message, String email, String username,
                             Long userId, String token, int expiresIn) {
        this.message = message;
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.token = token;
        this.expiresIn = expiresIn;
    }
}

