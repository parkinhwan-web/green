package com.example.demo.repository;

import com.example.demo.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // ✅ 일반 리스트용
    List<PointHistory> findByUserIdOrderByDateDesc(Long userId);

    // ✅ 페이징 지원용
    Page<PointHistory> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);
}
