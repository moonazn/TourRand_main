package com.tourbus.tourrand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private List<String> keywordList;

    public KeywordAdapter(List<String> keywordList) {
        this.keywordList = keywordList;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        holder.keywordTextView.setText(keywordList.get(position));
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    public static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView keywordTextView;

        public KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            keywordTextView = itemView.findViewById(R.id.keywordTextView);
        }
    }
}
