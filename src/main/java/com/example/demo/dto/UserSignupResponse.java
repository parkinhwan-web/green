package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupResponse {
    private String message;
    private Long userId;
    private String createdAt;
}
