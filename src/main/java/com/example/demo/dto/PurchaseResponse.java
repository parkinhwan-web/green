package com.example.demo.dto;

public class PurchaseResponse {
    private boolean success;
    private String message;
    private Long userCouponId;
    private int remainingPoints;
    private LocalDate expireDate;
    private String barcode;
    private CouponDetails couponDetails;

    @Data
    public static class CouponDetails {
        private String brandName;
        private String productName;
        private int pointsUsed;
    }
}
