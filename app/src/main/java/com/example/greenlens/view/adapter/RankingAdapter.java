package com.example.greenlens.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.User;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<User> userList = new ArrayList<>();

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking_user, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        User user = userList.get(position);

        // 순위 표시 (1-based index)
        int rank = position + 1;
        holder.textRank.setText(String.valueOf(rank));

        // 사용자 이름과 배출 횟수 표시
        holder.textUsername.setText(user.getUsername());
        holder.textRecycleCount.setText(String.format("%d회", user.getRecycleCount()));

        // 순위 배경 설정 (이미 XML에서 그라데이션으로 설정되어 있음)
        holder.rankBackground.setAlpha(rank <= 3 ? 1.0f : 0.7f);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView textRank;
        View rankBackground;
        TextView textUsername;
        TextView textRecycleCount;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.textRank);
            rankBackground = itemView.findViewById(R.id.rankBackground);
            textUsername = itemView.findViewById(R.id.textUsername);
            textRecycleCount = itemView.findViewById(R.id.textRecycleCount);
        }
    }
}