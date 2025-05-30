package com.example.demo.controller;

import com.example.demo.dto.PurchaseRequest;
import com.example.demo.dto.PurchaseResponse;
import com.example.demo.entity.Coupon;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return ResponseEntity.ok(couponService.getCoupons());
    }

    /** 쿠폰 구매 */
    @PostMapping("/coupons/{couponId}/purchase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PurchaseResponse> purchaseCoupon(
            @PathVariable Long couponId,
            @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PurchaseResponse response = couponService.purchaseCoupon(userDetails.getUser().getId(), couponId);
        return ResponseEntity.ok(response);
    }

    /** 쿠폰 사용 */
    @PostMapping("/users/{userId}/coupons/{couponId}/use")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> useCoupon(
            @PathVariable Long userId,
            @PathVariable Long couponId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (!userDetails.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        PurchaseResponse response = couponService.useCoupon(userId, couponId);
        return ResponseEntity.ok(response);
    }
}
