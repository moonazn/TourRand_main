package com.tourbus.tourrand;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripPlanViewHolder extends RecyclerView.ViewHolder {

    private TextView tripNameTextView;
    private TextView travelDateTextView;
    private TextView dDayTextView;
    private ImageView member1ImageView;
    private ImageView member2ImageView;
    private ImageView member3ImageView;
    private ImageView member4ImageView;
    private ImageView defaultImageView;

    public TripPlanViewHolder(@NonNull View itemView) {
        super(itemView);
        tripNameTextView = itemView.findViewById(R.id.trip_name_text_view);
        travelDateTextView = itemView.findViewById(R.id.travel_date_text_view);
        dDayTextView = itemView.findViewById(R.id.d_day_text_view);
        member1ImageView = itemView.findViewById(R.id.member1_image_view);
        member2ImageView = itemView.findViewById(R.id.member2_image_view);
        member3ImageView = itemView.findViewById(R.id.member3_image_view);
        member4ImageView = itemView.findViewById(R.id.member4_image_view);
        defaultImageView = itemView.findViewById(R.id.default_image_view);
    }

    public void bind(TripPlan tripPlan) {
        tripNameTextView.setText(tripPlan.getTripName());
        travelDateTextView.setText(tripPlan.getTravelDate());

        String dday;
        if(Integer.parseInt(tripPlan.getDDay()) >= 0) {
            dday = "D-" + tripPlan.getDDay();
        } else {
            dday = "D+" + tripPlan.getDDay();
        }

        dDayTextView.setText(dday);

        // 최대 4개의 멤버 프로필 사진 설정
        List<Integer> memberImages = tripPlan.getMemberImages();
        if (memberImages != null) {
            int size = memberImages.size();
            if (size >= 1) {
                member1ImageView.setImageResource(memberImages.get(0));
                member1ImageView.setVisibility(View.VISIBLE);
            } else {
                member1ImageView.setVisibility(View.GONE);
            }

            if (size >= 2) {
                member2ImageView.setImageResource(memberImages.get(1));
                member2ImageView.setVisibility(View.VISIBLE);
            } else {
                member2ImageView.setVisibility(View.GONE);
            }

            if (size >= 3) {
                member3ImageView.setImageResource(memberImages.get(2));
                member3ImageView.setVisibility(View.VISIBLE);
            } else {
                member3ImageView.setVisibility(View.GONE);
            }

            if (size >= 4) {
                member4ImageView.setImageResource(memberImages.get(3));
                member4ImageView.setVisibility(View.VISIBLE);
            } else {
                member4ImageView.setVisibility(View.GONE);
            }

            defaultImageView.setVisibility(View.GONE);
        } else {
//            // memberImages가 null인 경우
//            member1ImageView.setVisibility(View.GONE);
//            member2ImageView.setVisibility(View.GONE);
//            member3ImageView.setVisibility(View.GONE);
//            member4ImageView.setVisibility(View.GONE);
//            defaultImageView.setVisibility(View.GONE);
            member1ImageView.setVisibility(View.VISIBLE);
            member2ImageView.setVisibility(View.VISIBLE);
            member3ImageView.setVisibility(View.VISIBLE);

            member1ImageView.setImageResource(R.drawable.cat);
            member2ImageView.setImageResource(R.drawable.dog);
            member3ImageView.setImageResource(R.drawable.tiger);

        }
    }
}
