package com.example.demo.service;

import com.example.demo.dto.PurchaseResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserCouponRepository userCouponRepository;

    /** 모든 쿠폰 조회 */
    public List<Coupon> getCoupons() {
        return couponRepository.findAll();
    }

    /** 상품권 구매 */
    @Transactional
    public PurchaseResponse purchaseCoupon(Long userId, Long couponId) {

        // 사용자, 쿠폰 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // 사용자 포인트 조회 및 차감
        Point point = pointRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("포인트 정보가 없습니다."));

        int couponPoint = coupon.getPoints();
        if (point.getPoints() < couponPoint) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        point.setPoints(point.getPoints() - couponPoint);
        pointRepository.save(point);

        // 포인트 사용 이력 기록
        PointHistory history = new PointHistory();
        history.setUser(user);
        history.setPoints(-couponPoint);
        history.setReason("상품권 구매: " + coupon.getProductName());
        history.setBalance(point.getPoints());
        history.setDate(ZonedDateTime.now());
        history.setType("사용");
        history.setBrandName(coupon.getBrandName());
        pointHistoryRepository.save(history);

        // 사용자 쿠폰 저장
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setPurchasedAt(ZonedDateTime.now());
        userCoupon.setExpireDate(LocalDate.now().plusDays(coupon.getExpireDays()));
        userCoupon.setBarcode(UUID.randomUUID().toString().substring(0, 13));
        userCouponRepository.save(userCoupon);

        // 응답 생성
        PurchaseResponse.CouponDetails details = new PurchaseResponse.CouponDetails();
        details.setBrandName(coupon.getBrandName());
        details.setProductName(coupon.getProductName());
        details.setPointsUsed(couponPoint);

        PurchaseResponse response = new PurchaseResponse();
        response.setSuccess(true);
        response.setMessage("상품권 구매가 완료되었습니다.");
        response.setUserCouponId(userCoupon.getId());
        response.setRemainingPoints(point.getPoints());
        response.setExpireDate(userCoupon.getExpireDate());
        response.setBarcode(userCoupon.getBarcode());
        response.setCouponDetails(details);

        return response;
    }
}
