package com.example.demo.controller;

import com.example.demo.dto.RecycleLogRequest;
import com.example.demo.dto.RecycleLogResponse;
import com.example.demo.entity.RecycleAnalysisResult;
import com.example.demo.security.CustomUserDetails; // ⬅️ 반드시 너의 경로에 맞게 import
import com.example.demo.service.RecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final AtomicLong analysisIdGenerator = new AtomicLong(1);
    private final RecycleService recycleService;

    /**
     * ✅ 이미지 분석 요청 API (분석 + 포인트 지급)
     */
    @PostMapping("/analyze")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "이미지 파일이 비어 있습니다."));
        }

        long analysisId = analysisIdGenerator.getAndIncrement();
        Long userId = userDetails.getUser().getId(); // ⬅️ 로그인 사용자 ID 추출

        recycleService.analyzeAndSave(image, analysisId, userId);

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
    public ResponseEntity<?> recordRecycleLog(@RequestBody RecycleLogRequest request) {
        try {
            RecycleLogResponse response = recycleService.saveLog(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ✅ 분석 결과 조회 API
     */
    @GetMapping("/result/{analysis_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAnalysisResult(@PathVariable("analysis_id") Long analysisId) {
        try {
            RecycleAnalysisResult result = recycleService.getResultByAnalysisId(analysisId);
            Map<String, Object> response = new HashMap<>();
            response.put("analysis_id", result.getAnalysisId());
            response.put("category", result.getCategory());
            response.put("confidence", result.getConfidence());
            response.put("disposal_method", result.getDisposalMethod());
            response.put("created_at", result.getCreatedAt().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ✅ 테스트용: 분석 결과 더미 데이터 삽입 API
     */
    @PostMapping("/insert-dummy")
    public ResponseEntity<?> insertDummy() {
        RecycleAnalysisResult result = new RecycleAnalysisResult();
        result.setAnalysisId(456L);
        result.setCategory("플라스틱");
        result.setConfidence(0.95);
        result.setDisposalMethod("플라스틱 전용 수거함에 버려주세요.");
        recycleService.saveAnalysisResult(result);
        return ResponseEntity.ok(Map.of("message", "테스트용 분석 결과가 저장되었습니다."));
    }
}
