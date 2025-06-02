package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class RecycleLogResponse {
    private boolean success;
    private Long logId;
    private int pointsEarned;
    private int totalPoints;
    private long recycleCount;
    private String message;
    private String wasteTypeKorean;
    private Map<String, Object> rankChange;
}
