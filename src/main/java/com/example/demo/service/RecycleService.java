package com.example.demo.service;

import com.example.demo.dto.RecycleLogRequest;
import com.example.demo.dto.RecycleLogResponse;
import com.example.demo.entity.RecycleLog;
import com.example.demo.entity.RecycleAnalysisResult;
import com.example.demo.entity.Point;
import com.example.demo.repository.RecycleLogRepository;
import com.example.demo.repository.PointRepository;
import com.example.demo.repository.RecycleAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class RecycleService {

    private final RecycleLogRepository recycleLogRepository;
    private final RecycleAnalysisResultRepository recycleAnalysisResultRepository;
    private final PointRepository pointRepository;

    /**
     * ✅ 분리수거 기록 저장 및 포인트 적립
     */
    @Transactional
    public RecycleLogResponse saveLog(RecycleLogRequest request) {
        // 1. 로그 저장
        RecycleLog log = new RecycleLog();
        log.setUserId(request.getUserId());
        log.setAnalysisId(request.getAnalysisId());
        log.setDisposalCategory(request.getDisposalCategory());
        log.setDisposalMethod(request.getDisposalMethod());

        RecycleLog saved = recycleLogRepository.save(log);

        // 2. 포인트 100P 적립
        Point point = pointRepository.findByUserId(request.getUserId())
                .orElseGet(() -> new Point(request.getUserId(), 0));
        point.setPoints(point.getPoints() + 100);
        point.setUpdatedAt(ZonedDateTime.now());
        pointRepository.save(point);

        // 3. 응답
        return RecycleLogResponse.builder()
                .message("분리수거 기록이 저장되고 100포인트가 적립되었습니다.")
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
        // TODO: YOLO 모델 분석 결과 반영
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
