package com.example.greenlens.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.manager.UserManager;
import com.example.greenlens.model.Point;
import com.example.greenlens.util.DevLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointHistoryViewModel extends ViewModel {
    private static final String TAG = "PointHistoryViewModel";
    private final MutableLiveData<List<Map<String, Object>>> pointHistory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final ApiService apiService;
    private final UserManager userManager;

    public PointHistoryViewModel(UserManager userManager) {
        this.userManager = userManager;
        this.apiService = ApiClient.getInstance().getApiService();
    }

    public LiveData<List<Map<String, Object>>> getPointHistory() {
        return pointHistory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadPointHistory() {
        if (!userManager.isLoggedIn()) {
            error.setValue("로그인이 필요합니다.");
            return;
        }

        String authToken = userManager.getAuthToken();
        if (authToken == null || authToken.isEmpty()) {
            error.setValue("로그인 세션이 만료되었습니다.");
            return;
        }

        isLoading.setValue(true);
        DevLog.d(TAG, "포인트 내역 불러오기 시작...");

        apiService.getRecycleActivities(authToken).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> history = response.body();
                    DevLog.d(TAG, "포인트 내역 응답 성공: " + history.size() + "개 항목");

                    // category, disposalCategory가 모두 null인 항목은 제외
                    List<Map<String, Object>> filteredHistory = new ArrayList<>();
                    for (Map<String, Object> item : history) {
                        String category = (String) item.get("category");
                        String disposalCategory = (String) item.get("disposalCategory");
                        if (category == null && disposalCategory == null) {
                            // 아무것도 추가하지 않음 (제외)
                            continue;
                        }
                        filteredHistory.add(item);
                    }

                    Collections.reverse(filteredHistory); // 최신순 정렬
                    pointHistory.setValue(filteredHistory);
                    DevLog.d(TAG, "포인트 내역 " + filteredHistory.size() + "개 로드 완료");
                } else {
                    error.setValue("포인트 내역을 불러올 수 없습니다.");
                    DevLog.e(TAG, "포인트 내역 조회 실패: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            DevLog.e(TAG, "에러 응답: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("네트워크 오류: " + t.getMessage());
                DevLog.e(TAG, "포인트 내역 로드 실패: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
