package com.example.greenlens.repository;

import com.example.greenlens.api.ApiClient;
import com.example.greenlens.api.ApiService;
import com.example.greenlens.model.Point;
import com.example.greenlens.model.response.PointResponse;
import com.example.greenlens.model.request.PointUseRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointRepository {
    private static PointRepository instance;
    private final ApiService apiService;

    private PointRepository() {
        apiService = ApiClient.getInstance().getApiService();
    }

    public static synchronized PointRepository getInstance() {
        if (instance == null) {
            instance = new PointRepository();
        }
        return instance;
    }

    public void getUserPoints(String token, Long userId, PointCallback<PointResponse> callback) {
        apiService.getUserPoints("Bearer " + token, userId).enqueue(new Callback<PointResponse>() {
            @Override
            public void onResponse(Call<PointResponse> call, Response<PointResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("포인트 정보를 가져오는데 실패했습니다: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PointResponse> call, Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void usePoints(String token, Long userId, int pointsToUse, String reason, PointCallback<PointResponse> callback) {
        PointUseRequest request = new PointUseRequest(pointsToUse, reason);
        apiService.usePoints("Bearer " + token, userId, request).enqueue(new Callback<PointResponse>() {
            @Override
            public void onResponse(Call<PointResponse> call, Response<PointResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("포인트 사용에 실패했습니다: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PointResponse> call, Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public void getPointHistory(String token, PointCallback<List<Point>> callback) {
        apiService.getPointHistory("Bearer " + token).enqueue(new Callback<List<Point>>() {
            @Override
            public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("포인트 내역 조회에 실패했습니다: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Point>> call, Throwable t) {
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public interface PointCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
}