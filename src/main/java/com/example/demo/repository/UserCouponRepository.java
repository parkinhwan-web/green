package com.example.demo.repository;

import com.example.demo.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    // ✅ 특정 유저가 구매한 특정 쿠폰을 찾는 쿼리
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
}
