package com.example.demo.service;

import com.example.demo.entity.Coupon;
import com.example.demo.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getAvailableCoupons() {
        return couponRepository.findByAvailable("true");
}

}
