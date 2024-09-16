package com.tourbus.tourrand;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripPlanAdapter extends RecyclerView.Adapter<TripPlanViewHolder> {

    private Context context;
    private List<TripPlan> tripPlanList;
    private HomeFragment1 activity;


    int lastPosition = -1;

    public TripPlanAdapter(Context context, List<TripPlan> tripPlanList, HomeFragment1 activity) {
        this.context = context;
        this.tripPlanList = tripPlanList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TripPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_plan, parent, false);
        return new TripPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripPlanViewHolder holder, int position) {

        if (position >= tripPlanList.size()) {
            Log.e("TripPlanAdapter", "Invalid position during onBindViewHolder: " + position + ", Size: " + tripPlanList.size());
            return;
        }

        TripPlan tripPlan = tripPlanList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlanEditActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                context.startActivity(intent);
            }
        });

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            ((TripPlanViewHolder) holder).itemView.startAnimation(animation);

            tripPlanList.get(position);
            holder.bind(tripPlan);
        }

        // 'more' 버튼 클릭 리스너 설정
        holder.more.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.more);
            popupMenu.getMenuInflater().inflate(R.menu.menu_delete, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_delete) {
                    // 삭제 확인 다이얼로그 띄우기
                    new AlertDialog.Builder(context)
                            .setTitle("삭제 확인")
                            .setMessage("정말 삭제하시겠습니까?")
                            .setPositiveButton("확인", (dialog, which) -> {

                                // 서버에 삭제 요청 메서드를 호출
                                int tripId = tripPlanList.get(position).getTourId();  // TripPlan의 ID를 가져오기

                                Log.d("remove", String.valueOf(tripId));
                                Log.d("position", String.valueOf(position));


                                activity.deleteTripOnServer(tripId, position);

                                Log.d("연결 완", "삭제");
                                // 삭제 확인 시 해당 아이템 삭제
                                if (position >= 0 && position < tripPlanList.size()) {
                                    tripPlanList.remove(position);
                                    notifyDataSetChanged();
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, tripPlanList.size());
                                } else {
                                    Log.e("TripPlanAdapter", "Attempted to remove item at invalid index: " + position);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
                return true;
            });
            popupMenu.show();
        });
    }
    public void removeItem(int position) {
        if (position <= 0 || position >= tripPlanList.size()) {
            // 방어적인 프로그래밍: 잘못된 인덱스에 접근할 경우
            Log.e("TripPlanAdapter", "Attempted to remove item at invalid index: " + position + ", Size: " + tripPlanList.size());
            return;
        }
        tripPlanList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tripPlanList.size());
    }
    @Override
    public int getItemCount() {
        return tripPlanList.size();
    }
}
