package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecycleLogResponse {
    private String message;
    private Long logId;
    private String createdAt;
}
