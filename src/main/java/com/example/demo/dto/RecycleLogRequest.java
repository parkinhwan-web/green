package com.example.demo.dto;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor          // ✅ 추가
@AllArgsConstructor
@Getter
@Setter
public class RecycleLogRequest {
    private Long userId;
    private Long analysisId;
    private String disposalCategory;
    private String disposalMethod;
}
