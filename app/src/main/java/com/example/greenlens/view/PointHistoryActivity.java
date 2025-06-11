package com.example.greenlens.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenlens.R;
import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.databinding.ActivityPointHistoryBinding;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.util.DevLog;
import com.example.greenlens.view.adapter.PointHistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        String authToken = userManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            showError("로그인 세션이 만료되었습니다.");
            showEmptyView(true);
            showLoading(false);
            return;
        }

        DevLog.d(TAG, "분리수거 활동 내역 불러오기 시작...");

        apiService.getRecycleActivities(authToken).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Map<String, Object>> recycleActivities = response.body();

                        // 모든 항목을 표시하되, 카테고리가 있는 경우 해당 카테고리를 사용
                        List<Map<String, Object>> filteredActivities = new ArrayList<>();
                        for (Map<String, Object> activity : recycleActivities) {
                            String category = (String) activity.get("category");
                            String disposalCategory = (String) activity.get("disposalCategory");

                            // 카테고리가 없는 경우 "분리수거"로 설정
                            if (category == null && disposalCategory == null) {
                                activity.put("category", "분리수거");
                            }

                            filteredActivities.add(activity);
                        }

                        if (!filteredActivities.isEmpty()) {
                            Collections.reverse(filteredActivities); // 최신순으로 정렬
                            adapter.submitList(filteredActivities);
                            showEmptyView(false);
                            DevLog.d(TAG, "분리수거 활동 내역 " + filteredActivities.size() + "개 로드 완료");
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
