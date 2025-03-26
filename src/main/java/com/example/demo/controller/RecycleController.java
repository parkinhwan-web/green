package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/recycle")
@RequiredArgsConstructor
public class RecycleController {

    private final AtomicLong analysisIdGenerator = new AtomicLong(1); // 임시 분석 ID 생성기
    private final AtomicLong logIdGenerator = new AtomicLong(1000);   // 임시 로그 ID 생성기

    /**
     * ✅ 이미지 분석 요청 API
     */
    @PostMapping("/analyze")
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 요청 가능
    public ResponseEntity<?> analyzeImage(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "이미지 파일이 비어 있습니다."));
        }

        long analysisId = analysisIdGenerator.getAndIncrement();

        Map<String, Object> response = new HashMap<>();
        response.put("analysis_id", analysisId);
        response.put("status", "processing");
        response.put("message", "이미지 분석 요청이 접수되었습니다.");
        response.put("created_at", ZonedDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * ✅ 분리수거 활동 기록 API
     */
    @PostMapping("/log")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> recordRecycleLog(@RequestBody Map<String, Object> request) {
        // 간단한 유효성 검사
        if (!request.containsKey("user_id") ||
            !request.containsKey("analytic_id") ||
            !request.containsKey("disposal_category") ||
            !request.containsKey("disposal_method")) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "필수 항목이 누락되었습니다."));
        }

        long logId = logIdGenerator.getAndIncrement();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "분리수거 기록이 기록되었습니다.");
        response.put("log_id", logId);
        response.put("created_at", ZonedDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
