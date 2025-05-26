package com.example.demo.service;

import com.example.demo.dto.RecycleLogRequest;
import com.example.demo.dto.RecycleLogResponse;
import com.example.demo.entity.RecycleLog;
import com.example.demo.entity.RecycleAnalysisResult;
import com.example.demo.repository.RecycleLogRepository;
import com.example.demo.repository.PointRepository;
import com.example.demo.repository.RecycleAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Point;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class RecycleService {

    private final RecycleLogRepository recycleLogRepository;
    private final RecycleAnalysisResultRepository recycleAnalysisResultRepository;
    private final PointRepository pointRepository;

    /**
     * ✅ 분리수거 기록 저장
     */
    @Transactional
    public RecycleLogResponse saveLog(RecycleLogRequest request) {
        RecycleLog log = new RecycleLog();
        log.setUserId(request.getUserId());
        log.setAnalysisId(request.getAnalysisId());
        log.setDisposalCategory(request.getDisposalCategory());
        log.setDisposalMethod(request.getDisposalMethod());

        RecycleLog saved = recycleLogRepository.save(log);

        return RecycleLogResponse.builder()
                .message("분리수거 기록이 기록되었습니다.")
                .count(saved.getId())
                .createdAt(saved.getCreatedAt().toString())
                .build();
    }

    /**
     * ✅ 분석 결과 조회
     */
    @Transactional(readOnly = true)
    public RecycleAnalysisResult getResultByAnalysisId(Long analysisId) {
        return recycleAnalysisResultRepository.findByAnalysisId(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("해당 분석 결과가 없습니다."));
    }

    /**
     * ✅ 분석 결과 저장 (테스트용)
     */
    @Transactional
    public void saveAnalysisResult(RecycleAnalysisResult result) {
        recycleAnalysisResultRepository.save(result);
    }

    /**
     * ✅ 이미지 분석 및 결과 저장
     */
    @Transactional
    public void analyzeAndSave(MultipartFile image, long analysisId) {
        // TODO: 추후 YOLO 분석 코드로 교체 필요
        String category = "플라스틱";
        double confidence = 0.92;
        String disposalMethod = "플라스틱 전용 수거함에 버려주세요.";

        RecycleAnalysisResult result = new RecycleAnalysisResult();
        result.setAnalysisId(analysisId);
        result.setCategory(category);
        result.setConfidence(confidence);
        result.setDisposalMethod(disposalMethod);
        result.setCreatedAt(ZonedDateTime.now());

        recycleAnalysisResultRepository.save(result);
    }

    /**
     * ✅ 포인트 조회
     */
    public Point getUserPointInfo(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }
}
