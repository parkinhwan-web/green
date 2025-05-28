package com.example.demo.service;

import com.example.demo.entity.Coupon;
import com.example.demo.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 쿠폰 관련 비즈니스 로직을 처리하는 서비스.
 * 현재는 'available' 플래그를 제거했으므로
 * 모든 쿠폰을 그대로 반환합니다.
 *
 * 향후 유효기간·재고 등 추가 조건이 필요하면
 * 이 메서드 내부에 필터링 로직을 넣어 주세요.
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    /** 모든 쿠폰 조회 (필터링 없음) */
    public List<Coupon> getCoupons() {
        return couponRepository.findAll();
    }
}

