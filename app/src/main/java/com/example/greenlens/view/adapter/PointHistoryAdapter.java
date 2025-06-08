package com.example.greenlens.view.adapter;

import com.example.greenlens.R;
import com.example.greenlens.model.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.PointViewHolder> {

    private List<Point> pointList = new ArrayList<>();

    public void setPoints(List<Point> points) {
        pointList = points;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point_history, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        Point point = pointList.get(position);
        holder.bind(point);
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    public static class PointViewHolder extends RecyclerView.ViewHolder {
        private TextView textDate;
        private TextView textCategory;
        private TextView textEarnedPoint;
        private TextView textTotalPoint;

        public PointViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textCategory = itemView.findViewById(R.id.text_category);
            textEarnedPoint = itemView.findViewById(R.id.text_earned_point);
            textTotalPoint = itemView.findViewById(R.id.text_total_point);
        }

        public void bind(Point point) {
            textDate.setText(point.getDate());
            textCategory.setText(point.getCategory());
            textEarnedPoint.setText("100P");  
            textTotalPoint.setText(point.getTotalPoints() + "P");
        }
    }
}
