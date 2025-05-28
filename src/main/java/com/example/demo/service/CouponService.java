package com.example.demo.service;

import com.example.demo.entity.Coupon;
import com.example.demo.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;   // ✅ 그대로 둡니다

    /* ✨ 수정 포인트 */
    public List<Coupon> getCoupons() {                 // 메서드 이름만 간단히 변경
        return couponRepository.findAll();             // 조건-없는 전체 조회로 교체
    }
}

