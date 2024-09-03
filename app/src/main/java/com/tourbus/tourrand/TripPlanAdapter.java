package com.tourbus.tourrand;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripPlanAdapter extends RecyclerView.Adapter<TripPlanViewHolder> {

    private Context context;
    private List<TripPlan> tripPlanList;

    int lastPosition = -1;

    public TripPlanAdapter(Context context, List<TripPlan> tripPlanList) {
        this.context = context;
        this.tripPlanList = tripPlanList;
    }

    @NonNull
    @Override
    public TripPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_plan, parent, false);
        return new TripPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripPlanViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlanEditActivity.class);
                context.startActivity(intent);



            }
        });

        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            ((TripPlanViewHolder) holder).itemView.startAnimation(animation);

            TripPlan tripPlan = tripPlanList.get(position);
            holder.bind(tripPlan);
        }
    }

    @Override
    public int getItemCount() {
        return tripPlanList.size();
    }
}
