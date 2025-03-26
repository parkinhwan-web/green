package com.example.demo.repository;

import com.example.demo.entity.RecycleLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecycleLogRepository extends JpaRepository<RecycleLog, Long> {
}
