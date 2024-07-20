package com.tourbus.tourrand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DaysViewHolder> {

    private List<String> daysList;
    private int selectedPosition = 0; // 기본값을 0으로 설정
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DaysAdapter(List<String> daysList, OnItemClickListener onItemClickListener) {
        this.daysList = daysList;
        this.onItemClickListener = onItemClickListener;
        notifyItemChanged(0); // 첫 번째 아이템이 기본으로 선택된 상태 반영
    }

    @NonNull
    @Override
    public DaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false);
        return new DaysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysViewHolder holder, int position) {
        holder.dayText.setText(daysList.get(position));
        holder.itemView.setBackgroundColor(selectedPosition == position ?
                ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_day_background) :
                ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(position);
            onItemClickListener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    class DaysViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        DaysViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
