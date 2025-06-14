package com.example.demo.controller;

public class PointController {
    
}
package com.example.demo.controller;

import com.example.demo.entity.Point;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Point> getMyPoint(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        Point point = pointService.getUserPointInfo(userId);
        return ResponseEntity.ok(point);
    }
}
