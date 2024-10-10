package com.tourbus.tourrand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RMAdapter extends RecyclerView.Adapter<RMAdapter.RMViewHolder> {
    private List<RMItem> rmItemList;
    public RMAdapter(List<RMItem> rmItemList){
        if (rmItemList != null) {
            this.rmItemList = rmItemList;
        } else {
            this.rmItemList = new ArrayList<>(); // Initialize with an empty list if null
        }

    }
    @NonNull
    @Override
    public RMAdapter.RMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_rand,parent,false);
        return new RMViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull RMAdapter.RMViewHolder holder, int position) {
        RMItem rmItem = rmItemList.get(position);
        holder.randUserNickname.setText(rmItem.getRandUserNickname());
        holder.randCount.setText(rmItem.getRandCount());
    }

    @Override
    public int getItemCount() {
        return rmItemList.size();
    }
    // 데이터 세팅 메서드
    public void setItems(List<RMItem> items) {
        if (items != null) {
            this.rmItemList = items;
        } else {
            this.rmItemList = new ArrayList<>(); // null일 경우 빈 리스트로 설정
        }
        notifyDataSetChanged(); // 데이터가 변경되었음을 알림
    }

    public class RMViewHolder extends RecyclerView.ViewHolder {
        public TextView randUserNickname;
        public TextView randCount;
        public RMViewHolder(@NonNull View itemView) {
            super(itemView);
            randUserNickname = itemView.findViewById(R.id.randUserNickname);
            randCount = itemView.findViewById(R.id.randCount);
        }
    }
}
