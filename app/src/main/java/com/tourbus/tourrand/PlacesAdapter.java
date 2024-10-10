package com.tourbus.tourrand;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder> {

    private List<Place> placesList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Place place);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

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

        holder.time.setVisibility(View.GONE);

        String apiKey = "856b60d15352dfaae39da72e011fc9c3";

        if (position < placesList.size() - 1) {
            holder.mapIcon.setVisibility(View.VISIBLE);
            Place nextPlace = placesList.get(position + 1);

            String origin = place.getLongitude() + "," + place.getLatitude();
            String destination = nextPlace.getLongitude() + "," + nextPlace.getLatitude();
            Log.d("PlacesAdapter", "origin: " + origin + ", dest: " + destination);

            KakaoApiService service = ApiClient.createService();
            service.getDrivingTime(
                    apiKey,
                    origin,
                    destination,
                    "", // waypoints
                    "RECOMMEND", // priority
                    "GASOLINE", // car_fuel
                    false, // car_hipass
                    false, // alternatives
                    false // road_details
            ).enqueue(new Callback<KakaoResponse>() {
                @Override
                public void onResponse(Call<KakaoResponse> call, Response<KakaoResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("PlacesAdapter", response.body().toString()); // 응답 내용을 로그로 출력

                        int duration = response.body().getRoutes().get(0).getSummary().getDuration();
                        int minutes = duration / 60;
                        holder.time.setText(minutes + "분 소요 예정");
                    } else if (response.body() == null) {
                        Log.e("PlacesAdapter", "Response body is null");
                        holder.time.setText("시간 계산 불가");
                    } else {
                        Log.e("PlacesAdapter", "Response not successful or body is null");
                        holder.time.setText("시간 계산 불가");
                    }
                }

                @Override
                public void onFailure(Call<KakaoResponse> call, Throwable t) {
                    Log.e("PlacesAdapter", "api failure: "+ t.getMessage(), t);

                    holder.time.setText("오류 발생");
                }
            });

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

                String url = "kakaomap://route?sp=" + origin +
                        "&ep=" + destination + "&by=CAR";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);

            });
        } else {
            holder.mapIcon.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placesList != null ? placesList.size() : 0;
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
        TextView time;
        View line;

        PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            mapIcon = itemView.findViewById(R.id.mapIcon);
            time = itemView.findViewById(R.id.time);
            line = itemView.findViewById(R.id.line);
        }
    }
}
