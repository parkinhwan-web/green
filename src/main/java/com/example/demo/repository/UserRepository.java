package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ 이메일로 사용자 찾기 (대소문자 무시)
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email") String email);

    // ✅ 이메일 중복 확인
    boolean existsByEmail(String email);

    // ✅ 사용자 ID로 상세 정보 조회 (fetch join 활용 가능)
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdWithDetails(@Param("userId") Long userId);
}
