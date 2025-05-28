package com.example.demo.controller;

import com.example.demo.entity.Coupon;
import com.example.demo.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final CouponService couponService;

    /** 로그인한 사용자가 쿠폰 목록을 조회 */
    @GetMapping("/coupons")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Coupon>> getCoupons() {
        // ▶︎ getAvailableCoupons() → getCoupons() 로 변경
        return ResponseEntity.ok(couponService.getCoupons());
    }
}
