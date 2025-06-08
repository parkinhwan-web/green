package com.example.greenlens.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.greenlens.R;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.Coupon;
import com.example.greenlens.model.User;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;

import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopDetailActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView textBrand;
    private TextView textName;
    private TextView textPoint;
    private TextView textValidity;
    private TextView textGuide;
    private Button btnPurchase;
    private UserManager userManager;
    private Coupon coupon;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        // 유저 매니저 및 API 서비스 초기화
        userManager = UserManager.getInstance(this);
        apiService = ApiClient.getInstance().getApiService();

        // View 초기화
        imageProduct = findViewById(R.id.image_product);
        textBrand = findViewById(R.id.text_brand);
        textName = findViewById(R.id.text_name);
        textPoint = findViewById(R.id.text_point);
        textValidity = findViewById(R.id.text_validity);
        textGuide = findViewById(R.id.text_guide);
        btnPurchase = findViewById(R.id.btn_purchase);

        // 뒤로가기 버튼
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null) {
            Long couponId = intent.getLongExtra("COUPON_ID", -1L);
            String brandName = intent.getStringExtra("BRAND_NAME");
            String productName = intent.getStringExtra("PRODUCT_NAME");
            int points = intent.getIntExtra("POINTS", 0);
            String category = intent.getStringExtra("CATEGORY");
            int imageResId = intent.getIntExtra("IMAGE_RES_ID", 0);
            String expireDate = intent.getStringExtra("EXPIRE_DATE");

            // 가져온 데이터로 Coupon 객체 생성
            coupon = new Coupon(brandName, productName, points, category, expireDate, imageResId);
            if (couponId != -1L) {
                coupon.setId(couponId);
            }
            coupon.setValidityDays(30); // 기본 유효기간 설정

            // 데이터 설정
            setCouponData(coupon);

            // 구매 버튼 이벤트 설정
            setupPurchaseButton();
        } else {
            // Intent가 없는 경우 테스트용 더미 데이터 사용
            Coupon dummyCoupon = new Coupon(
                    "스타벅스",
                    "아메리카노",
                    4500,
                    "카페",
                    "2025-06-10까지",
                    R.drawable.ic_cafe
            );
            dummyCoupon.setId(4L); // 더미 데이터에도 ID 설정
            dummyCoupon.setValidityDays(30);
            coupon = dummyCoupon;

            // 데이터 설정
            setCouponData(dummyCoupon);

            // 구매 버튼 이벤트 설정
            setupPurchaseButton();
        }
    }

    private void setupPurchaseButton() {
        btnPurchase.setOnClickListener(v -> {
            // 사용자 포인트 확인
            User user = userManager.getCurrentUser();
            if (user != null) {
                int userPoints = user.getPoints();

                if (userPoints >= coupon.getPoints()) {
                    // 구매 확인 다이얼로그 표시
                    showPurchaseConfirmDialog(userPoints);
                } else {
                    // 포인트 부족 메시지
                    Toast.makeText(this,
                            "포인트가 부족합니다. 현재 보유 포인트: " + userPoints + "P",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // 로그인 필요 메시지
                Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPurchaseConfirmDialog(int userPoints) {
        new AlertDialog.Builder(this)
                .setTitle("상품권 구매")
                .setMessage(coupon.getProductName() + "을(를) " + coupon.getPoints() + "P로 구매하시겠습니까?\n" +
                        "구매 후 남은 포인트: " + (userPoints - coupon.getPoints()) + "P")
                .setPositiveButton("구매", (dialog, which) -> {
                    // 구매 처리 로직
                    performPurchase();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void performPurchase() {
        // 로그인 확인
        if (!userManager.isLoggedIn()) {
            Toast.makeText(this, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = userManager.getAuthToken();

        // 구매 API 요청 데이터 생성
        Map<String, Object> purchaseRequest = new HashMap<>();
        purchaseRequest.put("brandName", coupon.getBrandName());
        purchaseRequest.put("productName", coupon.getProductName());
        purchaseRequest.put("points", coupon.getPoints());
        purchaseRequest.put("category", coupon.getCategory());
        purchaseRequest.put("expireDate", coupon.getExpireDate());

        // 구매 API 호출
        apiService.purchaseCoupon(authToken, coupon.getId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    boolean success = (Boolean) result.getOrDefault("success", false);

                    if (success) {
                        // 사용자 포인트 업데이트
                        User currentUser = userManager.getCurrentUser();
                        if (currentUser != null) {
                            double remainingPoints = (double) result.get("remainingPoints");
                            currentUser.setPoints((int) remainingPoints);
                            userManager.saveUser(currentUser);
                        }

                        Toast.makeText(ShopDetailActivity.this,
                                coupon.getProductName() + " 구매가 완료되었습니다!",
                                Toast.LENGTH_SHORT).show();

                        // 구매 후 쿠폰함 화면으로 이동
                        Intent intent = new Intent(ShopDetailActivity.this, CouponHistoryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = (String) result.getOrDefault("message", "구매에 실패했습니다.");
                        Toast.makeText(ShopDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShopDetailActivity.this,
                            "구매 처리 중 오류가 발생했습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ShopDetailActivity.this,
                        "네트워크 오류: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCouponData(Coupon coupon) {
        if (coupon != null) {
            // 이미지 설정
            if (coupon.getImageUrl() != null && !coupon.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(coupon.getImageUrl())
                        .into(imageProduct);
            } else if (coupon.getImageResId() != 0) {
                imageProduct.setImageResource(coupon.getImageResId());
            }

            // 브랜드명 설정
            textBrand.setText(coupon.getBrandName());

            // 상품명 설정
            textName.setText(String.format("[%s] %s",
                    coupon.getBrandName(),
                    coupon.getProductName()));

            // 포인트 설정
            textPoint.setText(String.format("%dP", coupon.getPoints()));

            // 유효기간 설정
            textValidity.setText(String.format("유효기간 %d일",
                    coupon.getValidityDays()));

            // 이용안내 설정
            textGuide.setText(getString(R.string.shop_detail_guide,
                    coupon.getBrandName()));
        }
    }
}