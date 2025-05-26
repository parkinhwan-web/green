package com.example.demo.service;

import com.example.demo.dto.RecycleLogRequest;
import com.example.demo.dto.RecycleLogResponse;
import com.example.demo.entity.RecycleLog;
import com.example.demo.entity.RecycleAnalysisResult;
import com.example.demo.entity.Point;
import com.example.demo.entity.PointHistory;
import com.example.demo.repository.RecycleLogRepository;
import com.example.demo.repository.RecycleAnalysisResultRepository;
import com.example.demo.repository.PointRepository;
import com.example.demo.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@RequiredArgsConstructor
@Service
public class RecycleService {

    private static final Logger log = LoggerFactory.getLogger(RecycleService.class);
    private final RecycleLogRepository recycleLogRepository;
    private final RecycleAnalysisResultRepository recycleAnalysisResultRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

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

    @Value("${app.upload-dir}")          // 🔸 프로퍼티 주입
    private String uploadDir;

    /**
     * ✅ 이미지 분석 및 결과 저장 + 포인트 지급 + 내역 기록
     */
    @Transactional
    public void analyzeAndSave(MultipartFile image, long analysisId, Long userId) {
        try {
            /* === (1) 디렉토리 확보 === */
        Path uploadPath = Paths.get(uploadDir)      // app.upload-dir 프로퍼티 주입
                               .toAbsolutePath()
                               .normalize();
        Files.createDirectories(uploadPath);        // 없으면 자동 생성

        /* === (2) 고유 파일명 === */
        String fileName = UUID.randomUUID() + "_" +
                          StringUtils.cleanPath(image.getOriginalFilename());
        Path target = uploadPath.resolve(fileName);

        /* === (3) 저장 === */
        image.transferTo(target);                   // 〈― FileNotFoundException 해결

        /* === (4) Python 스크립트 실행 === */
        String pythonOutput = runPythonScript(target.toString());

            // 2) 파싱 로직 (stdout 포맷에 맞게 조정)
            String category;
            double confidence;
            String disposalMethod;
            if (pythonOutput.startsWith("error:")) {
                category = "unknown";
                confidence = 0.0;
                disposalMethod = "분석 실패";
            } else {
                List<String> lines = pythonOutput.lines().collect(Collectors.toList());
                category        = lines.get(0).trim();
                confidence      = lines.size() > 1 ? Double.parseDouble(lines.get(1).trim()) : 0.0;
                disposalMethod  = lines.size() > 2 ? lines.get(2).trim() : "";
            }

            // 3) 분석 결과 저장
            RecycleAnalysisResult result = new RecycleAnalysisResult();
            result.setAnalysisId(analysisId);
            result.setCategory(category);
            result.setConfidence(confidence);
            result.setDisposalMethod(disposalMethod);
            result.setCreatedAt(ZonedDateTime.now());
            recycleAnalysisResultRepository.save(result);

            // --- 5) 포인트 지급 ---
            Point point = pointRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Point p = new Point();
                        p.setUserId(userId);
                        p.setPoints(0);
                        return p;
                    });

            point.setPoints(point.getPoints() + 100);
            point.setUpdatedAt(ZonedDateTime.now());
            pointRepository.save(point);

            // 포인트 지급 내역 기록
            PointHistory history = new PointHistory();
            history.setUserId(userId);
            history.setDate(ZonedDateTime.now());
            history.setType("적립");
            history.setReason("AI 분석 리워드");
            history.setWasteTypeKorean(category); // 분석 결과에서 온 항목
            history.setPoints(100);
            history.setBalance(point.getPoints());
            pointHistoryRepository.save(history);

            // 콘솔 로그
            System.out.println("🎉 [포인트 지급] userId=" + userId + ", 현재 포인트=" + point.getPoints());
        } catch (IOException e) {
            log.error("이미지 처리 중 IOException 발생: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 처리 중 오류 발생", e);
        }
    }

    /**
     * Python 스크립트(recycle.py)를 호출하고 stdout 전체를 문자열로 반환
     */
    private String runPythonScript(String imagePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "python3", "scripts/recycle.py", imagePath
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return "error: script failed with exit code " + exitCode;
            }
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    /**
     * ✅ 포인트 조회
     */
    public Point getUserPointInfo(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보가 없습니다."));
    }
}
