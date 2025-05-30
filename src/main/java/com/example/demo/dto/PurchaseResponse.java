package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class PurchaseResponse {
    private boolean success;
    private String message;
    private Long userCouponId;
    private int remainingPoints;

    // ✅ 쿠폰 사용용으로 수정된 필드
    private ZonedDateTime usedDate;
    private String couponImageUrl;
    private String couponImageBase64;

    private LocalDate expireDate;
    private String barcode;
    private CouponDetails couponDetails;

    @Data
    public static class CouponDetails {
        private String brandName;
        private String productName;
        private int pointsUsed;

        // ✅ 쿠폰 사용용으로 추가된 필드
        private String barcode;
        private LocalDate expireDate;
        private String usageInstructions;
    }
}

