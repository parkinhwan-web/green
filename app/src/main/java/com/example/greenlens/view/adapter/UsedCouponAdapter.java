package com.example.greenlens.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenlens.R;
import com.example.greenlens.model.Coupon;

import java.util.ArrayList;
import java.util.List;

public class UsedCouponAdapter extends RecyclerView.Adapter<UsedCouponAdapter.CouponViewHolder> {
    private List<Coupon> coupons = new ArrayList<>();

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_used_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.bind(coupon);
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        private final ImageView couponImage;
        private final TextView brandText;
        private final TextView nameText;
        private final TextView dateText;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            couponImage = itemView.findViewById(R.id.image_coupon);
            brandText = itemView.findViewById(R.id.text_brand);
            nameText = itemView.findViewById(R.id.text_name);
            dateText = itemView.findViewById(R.id.text_date);
        }

        public void bind(Coupon coupon) {
            String formattedName = String.format("[%s] %s",
                    coupon.getBrandName(),
                    coupon.getProductName());  // getName() -> getProductName()으로 수정

            brandText.setText(coupon.getBrandName());
            nameText.setText(formattedName);
            dateText.setText(coupon.getExpireDate() + "까지");

            if (coupon.getImageResId() != 0) {
                couponImage.setImageResource(coupon.getImageResId());
            }

            // 사용된 쿠폰은 전체적으로 흐리게 표시
            itemView.setAlpha(0.5f);
        }
    }
}