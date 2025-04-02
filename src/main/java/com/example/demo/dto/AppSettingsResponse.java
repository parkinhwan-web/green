package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppSettingsResponse {
    private String theme;
    private boolean notifications;
    private String language;
}
