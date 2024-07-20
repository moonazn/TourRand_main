package com.tourbus.tourrand;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {

    private List<Place> placesList;

    public PlacesAdapter(List<Place> placesList) {
        this.placesList = placesList;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        Place place = placesList.get(position);
        holder.placeName.setText(place.getPlaceName());
        holder.placeAddress.setText(place.getAddress());

        if (position < placesList.size() - 1) {
            holder.mapIcon.setVisibility(View.VISIBLE);
            holder.mapIcon.setOnClickListener(v -> {
                // 카카오맵 실행: 두 장소 사이의 경로를 표시
                Context context = v.getContext();
                Place nextPlace = placesList.get(position + 1);
                String url = "kakaomap://route?sp=" + Uri.encode(place.getAddress()) +
                        "&ep=" + Uri.encode(nextPlace.getAddress());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            });
        } else {
            holder.mapIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView placeAddress;
        ImageView mapIcon;

        PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            mapIcon = itemView.findViewById(R.id.mapIcon);
        }
    }
}
