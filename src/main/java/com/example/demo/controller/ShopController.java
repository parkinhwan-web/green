@GetMapping("/coupons")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<Coupon>> getCoupons() {
    return ResponseEntity.ok(couponService.getAvailableCoupons());
}
