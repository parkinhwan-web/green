package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecycleLogRequest {
    private Long userId;
    private Long analysisId;
    private String disposalCategory;
    private String disposalMethod;
}
