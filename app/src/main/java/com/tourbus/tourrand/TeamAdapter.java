package com.tourbus.tourrand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.sdk.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private List<TeamItem> teamItem;
    public TeamAdapter(List<TeamItem> teamItem){
        String userNickname = UserManager.getInstance().getUserNickname();
        TeamItem userItem = new TeamItem(userNickname);

        this.teamItem = new ArrayList<>();
        this.teamItem.add(userItem);
        this.teamItem.addAll(teamItem);
    }
    @NonNull
    @Override
    public TeamAdapter.TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member,parent,false);
        return new TeamViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamAdapter.TeamViewHolder holder, int position) {
        TeamItem teamList = teamItem.get(position);
        holder.member.setText(teamList.getMember());

    }

    @Override
    public int getItemCount() {
        return teamItem.size();
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder {
        public TextView member;
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            member = itemView.findViewById(R.id.memberTxt);
        }
    }
}
