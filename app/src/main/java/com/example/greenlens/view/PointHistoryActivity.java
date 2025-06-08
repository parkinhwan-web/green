package com.example.greenlens.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenlens.R;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivityPointHistoryBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.Point;
import com.example.greenlens.model.User;
import com.example.greenlens.util.DevLog;
import com.example.greenlens.view.adapter.PointHistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointHistoryActivity extends AppCompatActivity {
    private ActivityPointHistoryBinding binding;
    private PointHistoryAdapter adapter;
    private UserManager userManager;
    private ApiService apiService;
    private static final String TAG = "PointHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼 설정
        binding.btnBack.setOnClickListener(v -> finish());

        userManager = UserManager.getInstance(this);
        apiService = ApiClient.getInstance().getApiService();

        setupRecyclerView();
        loadRecycleHistory();
    }

    private void setupRecyclerView() {
        adapter = new PointHistoryAdapter();
        binding.recyclerPointHistory.setAdapter(adapter);
        binding.recyclerPointHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadRecycleHistory() {
        showLoading(true);

        if (!userManager.isLoggedIn()) {
            showError("로그인이 필요합니다.");
            showEmptyView(true);
            showLoading(false);
            return;
        }

        User currentUser = userManager.getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            showError("사용자 정보를 찾을 수 없습니다.");
            showEmptyView(true);
            showLoading(false);
            return;
        }

        String authToken = userManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            showError("로그인 세션이 만료되었습니다.");
            showEmptyView(true);
            showLoading(false);
            return;
        }

        DevLog.d(TAG, "분리수거 활동 내역 불러오기 시작...");

        apiService.getRecycleActivities(authToken, currentUser.getUserId()).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Map<String, Object>> recycleActivities = response.body();
                        if (!recycleActivities.isEmpty()) {
                            List<Point> pointList = convertToPointList(recycleActivities);
                            Collections.reverse(pointList); // 최신순으로 정렬
                            adapter.setPoints(pointList);
                            showEmptyView(false);
                            DevLog.d(TAG, "분리수거 활동 내역 " + pointList.size() + "개 로드 완료");
                        } else {
                            showEmptyView(true);
                            DevLog.d(TAG, "분리수거 활동 내역이 없습니다.");
                        }
                    } else {
                        showEmptyView(true);
                        DevLog.e(TAG, "분리수거 활동 내역 조회 실패: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("네트워크 오류: " + t.getMessage());
                    showEmptyView(true);
                    DevLog.e(TAG, "분리수거 활동 내역 로드 실패: " + t.getMessage());
                });
            }
        });
    }

    private List<Point> convertToPointList(List<Map<String, Object>> recycleActivities) {
        List<Point> pointList = new ArrayList<>();
        User currentUser = userManager.getCurrentUser();
        int currentTotalPoints = currentUser != null ? currentUser.getPoints() : 0;

        // 활동 개수만큼 뒤로 계산하여 각 시점의 포인트 계산
        int activitiesCount = recycleActivities.size();

        for (int i = 0; i < recycleActivities.size(); i++) {
            Map<String, Object> activity = recycleActivities.get(i);
            try {
                Point point = new Point();

                // 날짜 설정 (activity에서 날짜 정보 가져오기, 없으면 현재 날짜)
                String dateStr = (String) activity.get("created_at");
                if (dateStr != null) {
                    point.setDate(formatDate(dateStr));
                } else {
                    point.setDate(getCurrentDate());
                }

                // 분류 설정
                String disposalCategory = (String) activity.get("disposal_category");
                point.setCategory(getWasteTypeKorean(disposalCategory));

                // 포인트 설정 (100P 고정)
                point.setEarnedPoints(100);

                // 각 시점의 누적 포인트 계산
                // 현재 포인트에서 남은 활동들의 포인트를 빼서 해당 시점의 포인트 계산
                int pointsAtThisTime = currentTotalPoints - (activitiesCount - i - 1) * 100;
                point.setTotalPoints(pointsAtThisTime);

                pointList.add(point);
            } catch (Exception e) {
                DevLog.e(TAG, "분리수거 활동 변환 오류", e);
            }
        }

        return pointList;
    }

    private String formatDate(String dateStr) {
        try {
            // API에서 받은 날짜 형식에 맞게 파싱하여 표시 형식으로 변환
            // 예: "2024-11-21T10:30:00" -> "2024-11-21"
            if (dateStr.contains("T")) {
                dateStr = dateStr.split("T")[0];
            }
            return dateStr;
        } catch (Exception e) {
            return getCurrentDate();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getWasteTypeKorean(String wasteType) {
        switch (wasteType != null ? wasteType.toLowerCase() : "") {
            case "plastic":
                return "플라스틱";
            case "paper":
                return "종이";
            case "glass":
                return "유리";
            case "metal":
                return "금속";
            case "vinyl":
                return "비닐";
            case "styrofoam":
                return "스티로폼";
            default:
                return wasteType != null ? wasteType : "분리수거";
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showEmptyView(boolean isEmpty) {
        binding.recyclerPointHistory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
