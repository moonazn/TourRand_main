package com.tourbus.tourrand;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
//                if (isAppInstalled(context, "net.daum.android.map")) {
//                    // 카카오맵이 설치되어 있는 경우
//                    Place nextPlace = placesList.get(position + 1);
//                    String url = "kakaomap://route?sp=" + "37.53723,127.00551" +
//                            "&ep=" + "37.49795,127.027637&by=CAR";  //PUBLICTRANSIT
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    context.startActivity(intent);
//                } else {
//                    //                    // 카카오맵 설치 페이지로 이동
//                    String marketUrl = "market://details?id=net.daum.android.map";
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
//                    context.startActivity(intent);
//                }

//                if(!isAppInstalled(context, "net.daum.android.map")){
////                     카카오맵이 설치되어 있지 않은 경우
//                    Toast.makeText(context, "카카오맵이 설치되어 있지 않습니다.", Toast.LENGTH_LONG).show();
//                }

                try {
                    //카카오맵이 설치되어있지 않으면 예외 뜸 ..‼️
                    Place nextPlace = placesList.get(position + 1);
                    String url = "kakaomap://route?sp=" + "37.53723,127.00551" +
                            "&ep=" + "37.49795,127.027637&by=CAR";  //PUBLICTRANSIT
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    System.out.println("exeption is : " + e);
                    // 카카오맵 설치 페이지로 이동
                    String marketUrl = "market://details?id=net.daum.android.map";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
                    context.startActivity(intent);
                }


            });
        } else {
            holder.mapIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    private boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
