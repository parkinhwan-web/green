package com.example.demo.service;

import com.example.demo.dto.RecycleLogRequest;
import com.example.demo.dto.RecycleLogResponse;
import com.example.demo.entity.RecycleLog;
import com.example.demo.entity.RecycleAnalysisResult;
import com.example.demo.repository.RecycleLogRepository;
import com.example.demo.repository.RecycleAnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecycleService {

    private final RecycleLogRepository recycleLogRepository;
    private final RecycleAnalysisResultRepository analysisResultRepository;

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

    @Transactional(readOnly = true)
    public RecycleAnalysisResult getResultByAnalysisId(Long analysisId) {
        return analysisResultRepository.findByAnalysisId(analysisId)
                .orElseThrow(() -> new IllegalArgumentException("해당 분석 결과가 없습니다."));
    }
}
