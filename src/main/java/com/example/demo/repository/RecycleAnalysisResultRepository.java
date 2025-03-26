package com.example.demo.repository;

import com.example.demo.entity.RecycleAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecycleAnalysisResultRepository extends JpaRepository<RecycleAnalysisResult, Long> {
    Optional<RecycleAnalysisResult> findByAnalysisId(Long analysisId);
}
