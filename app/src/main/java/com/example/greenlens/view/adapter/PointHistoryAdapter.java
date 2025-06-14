package com.example.greenlens.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.util.DevLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PointHistoryAdapter extends ListAdapter<Map<String, Object>, PointHistoryAdapter.PointHistoryViewHolder> {

    public PointHistoryAdapter() {
        super(new DiffUtil.ItemCallback<Map<String, Object>>() {
            @Override
            public boolean areItemsTheSame(@NonNull Map<String, Object> oldItem, @NonNull Map<String, Object> newItem) {
                return oldItem.get("logId").equals(newItem.get("logId"));
            }

            @Override
            public boolean areContentsTheSame(@NonNull Map<String, Object> oldItem, @NonNull Map<String, Object> newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public PointHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_point_history, parent, false);
        return new PointHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointHistoryViewHolder holder, int position) {
        Map<String, Object> history = getItem(position);
        holder.bind(history);
    }

    static class PointHistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textDate;
        private final TextView textCategory;
        private final TextView textEarnedPoint;
        private final TextView textTotalPoint;
        private final SimpleDateFormat dateFormat;

        public PointHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textCategory = itemView.findViewById(R.id.text_category);
            textEarnedPoint = itemView.findViewById(R.id.text_earned_point);
            textTotalPoint = itemView.findViewById(R.id.text_total_point);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        public void bind(Map<String, Object> history) {
            try {
                // 날짜 설정
                String createdAt = (String) history.get("createdAt");
                if (createdAt != null) {
                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                            .parse(createdAt);
                    if (date != null) {
                        textDate.setText(dateFormat.format(date));
                    }
                }

                // 카테고리 설정
                String category = (String) history.get("category");
                if (category != null && !category.isEmpty()) {
                    textCategory.setText(category);
                    textCategory.setVisibility(View.VISIBLE);
                } else {
                    textCategory.setText("분리수거");
                    textCategory.setVisibility(View.VISIBLE);
                }

                // 포인트 설정
                Object totalPointsObj = history.get("totalPoints");
                if (totalPointsObj != null) {
                    long totalPoints;
                    if (totalPointsObj instanceof Integer) {
                        totalPoints = ((Integer) totalPointsObj).longValue();
                    } else if (totalPointsObj instanceof Long) {
                        totalPoints = (Long) totalPointsObj;
                    } else if (totalPointsObj instanceof Double) {
                        totalPoints = ((Double) totalPointsObj).longValue();
                    } else {
                        totalPoints = 0;
                    }
                    textTotalPoint.setText(String.format("%dP", totalPoints));
                } else {
                    textTotalPoint.setText("0P");
                }

                // 적립 포인트는 항상 100P로 표시
                textEarnedPoint.setText("1000P");

            } catch (Exception e) {
                DevLog.e("PointHistoryAdapter", "데이터 바인딩 중 오류 발생", e);
            }
        }
    }
}
