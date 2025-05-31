package com.example.demo.repository;

import com.example.demo.entity.RecycleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecycleLogRepository extends JpaRepository<RecycleLog, Long> {

    // ✅ 사용자별 분리수거 횟수 집계 (user_id, count)
    @Query("SELECT r.user.id, COUNT(r) FROM RecycleLog r GROUP BY r.user.id")
    List<Object[]> countRecycleLogsByUser();

    // ✅ 전체 유저 수 (분리수거 기록이 있는 유저 수)
    @Query("SELECT COUNT(DISTINCT r.user.id) FROM RecycleLog r")
    Long countDistinctUsers();

    // ✅ 전체 분리수거 횟수
    @Query("SELECT COUNT(r) FROM RecycleLog r")
    Long countTotalRecycles();
}
