package com.tourbus.tourrand;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
    ImageView more;
    private Context context;

    public TripPlanViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        tripNameTextView = itemView.findViewById(R.id.trip_name_text_view);
        travelDateTextView = itemView.findViewById(R.id.travel_date_text_view);
        dDayTextView = itemView.findViewById(R.id.d_day_text_view);
        member1ImageView = itemView.findViewById(R.id.member1_image_view);
        member2ImageView = itemView.findViewById(R.id.member2_image_view);
        member3ImageView = itemView.findViewById(R.id.member3_image_view);
        member4ImageView = itemView.findViewById(R.id.member4_image_view);
        defaultImageView = itemView.findViewById(R.id.default_image_view);
        more = itemView.findViewById(R.id.more);
        this.context = context;

    }

    public void bind(TripPlan tripPlan) {
        tripNameTextView.setText(tripPlan.getTripName());
        travelDateTextView.setText(tripPlan.getTravelDate());

        String dday;
        if(Integer.parseInt(tripPlan.getDDay()) >= 0) {
            dday = "D-" + tripPlan.getDDay();
        } else {
            dday = "D+" + Math.abs(Integer.parseInt(tripPlan.getDDay()));
        }

        dDayTextView.setText(dday);

        // 최대 4개의 멤버 프로필 사진 설정
        List<String> memberImages = tripPlan.getMemberImages();
        if (memberImages != null) {
            int size = memberImages.size();
            if (size >= 1) {
                Glide.with(context)
                        .load(memberImages.get(0)) // URL 로드
                        .placeholder(R.drawable.cat) // 로딩 중에 표시할 이미지
                        .error(R.drawable.cat) // 오류 발생 시 표시할 이미지
                        .into(member1ImageView);
                member1ImageView.setVisibility(View.VISIBLE);
            } else {
                member1ImageView.setVisibility(View.GONE);
            }

            if (size >= 2) {
                Glide.with(context)
                        .load(memberImages.get(1)) // URL 로드
                        .placeholder(R.drawable.cat) // 로딩 중에 표시할 이미지
                        .error(R.drawable.dog) // 오류 발생 시 표시할 이미지
                        .into(member2ImageView);
                member2ImageView.setVisibility(View.VISIBLE);
            } else {
                member2ImageView.setVisibility(View.GONE);
            }

            if (size >= 3) {
                Glide.with(context)
                        .load(memberImages.get(2)) // URL 로드
                        .placeholder(R.drawable.tiger) // 로딩 중에 표시할 이미지
                        .error(R.drawable.cat) // 오류 발생 시 표시할 이미지
                        .into(member3ImageView);
                member3ImageView.setVisibility(View.VISIBLE);
            } else {
                member3ImageView.setVisibility(View.GONE);
            }

            if (size >= 4) {
                Glide.with(context)
                        .load(memberImages.get(3)) // URL 로드
                        .placeholder(R.drawable.elephant) // 로딩 중에 표시할 이미지
                        .error(R.drawable.cat) // 오류 발생 시 표시할 이미지
                        .into(member4ImageView);
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
