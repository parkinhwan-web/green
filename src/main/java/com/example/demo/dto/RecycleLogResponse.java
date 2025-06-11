package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class RecycleLogResponse {
    private Long id;
    private Long analysisId;
    private String category;
    private String disposalCategory;
    private String disposalMethod;
    private ZonedDateTime createdAt;
}