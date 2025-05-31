package com.example.demo.service;

import com.example.demo.entity.User;
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
import com.example.demo.repository.UserRepository;
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
import java.text.Normalizer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RequiredArgsConstructor
@Service
public class RecycleService {

    private static final Logger log = LoggerFactory.getLogger(RecycleService.class);
    private final RecycleLogRepository recycleLogRepository;
    private final RecycleAnalysisResultRepository recycleAnalysisResultRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository; // ✅ 추가됨

    /**
     * ✅ 분리수거 기록 저장
     */
    @Transactional
    public RecycleLogResponse saveLog(RecycleLogRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RecycleLog log = new RecycleLog();
        log.setUser(user); // ✅ userId 대신 연관관계로 설정
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

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${script.python-exe}")
    private String pythonExe;

    @Value("${script.path}")
    private String scriptPath;

    @Value("${script.weight}")
    private String weightPath;

    /**
     * ✅ 이미지 분석 및 결과 저장 + 포인트 지급 + 내역 기록
     */
    @Transactional
    public void analyzeAndSave(MultipartFile image, long analysisId, Long userId) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String original = StringUtils.getFilename(image.getOriginalFilename());
            String ascii = Normalizer.normalize(original, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "")
                    .replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID() + "_" + ascii;
            Path target = uploadPath.resolve(fileName);

            image.transferTo(target);
            if (!Files.exists(target)) {
                throw new IllegalStateException("파일 저장 실패: " + target);
            }

            String pythonOutput = runPythonScript(target.toString());
            log.warn("🔍 PY FULL OUTPUT\n{}", pythonOutput);

            String category;
            double confidence;
            String disposalMethod;
            if (pythonOutput.startsWith("error:")) {
                category = "unknown";
                confidence = 0.0;
                disposalMethod = "분석 실패";
            } else {
                List<String> lines = pythonOutput.lines().collect(Collectors.toList());
                category = lines.get(0).trim();
                confidence = lines.size() > 1 ? Double.parseDouble(lines.get(1).trim()) : 0.0;
                disposalMethod = lines.size() > 2 ? lines.get(2).trim() : "";
            }

            RecycleAnalysisResult result = new RecycleAnalysisResult();
            result.setAnalysisId(analysisId);
            result.setCategory(category);
            result.setConfidence(confidence);
            result.setDisposalMethod(disposalMethod);
            result.setCreatedAt(ZonedDateTime.now());
            recycleAnalysisResultRepository.save(result);

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

            User user = new User();
            user.setId(userId);

            PointHistory history = new PointHistory();
            history.setUser(user);
            history.setDate(ZonedDateTime.now());
            history.setType("적립");
            history.setReason("AI 분석 리워드");
            history.setWasteTypeKorean(category);
            history.setPoints(100);
            history.setBalance(point.getPoints());

            pointHistoryRepository.save(history);

            System.out.println("🎉 [포인트 지급] userId=" + userId + ", 현재 포인트=" + point.getPoints());
        } catch (IOException e) {
            log.error("이미지 처리 중 IOException 발생: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 처리 중 오류 발생", e);
        }
    }

    private String runPythonScript(String imgPath) {
        ProcessBuilder pb = new ProcessBuilder(
                pythonExe,
                scriptPath,
                "--image", imgPath
        );
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();
            String output;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                output = br.lines().collect(Collectors.joining("\n"));
            }
            int exit = proc.waitFor();
            log.info("PYTHON exit={} cmd={}", exit, pb.command());

            if (exit != 0) {
                return "error: script failed with exit code " + exit + "\n" + output;
            }
            return output;
        } catch (IOException | InterruptedException e) {
            return "error:" + e.getMessage();
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
