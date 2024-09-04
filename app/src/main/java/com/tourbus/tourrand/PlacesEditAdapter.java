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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesEditAdapter extends RecyclerView.Adapter<PlacesEditAdapter.PlacesViewHolder>
        implements ItemMoveCallback.ItemTouchHelperAdapter {

    private List<Place> placesList;
    private boolean isEditing = false;
    private DataChangeListener dataChangeListener;

    // DataChangeListener 인터페이스 정의
    public interface DataChangeListener {
        void onDataChanged(List<Place> updatedPlacesList);
    }
    public PlacesEditAdapter(List<Place> placesList, DataChangeListener dataChangeListener) {
        this.placesList = placesList;
        this.dataChangeListener = dataChangeListener;
    }
    public PlacesEditAdapter(List<Place> placesList) {
        this.placesList = placesList;
    }

    public void setEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }
    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place_edit, parent, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        Place place = placesList.get(position);
        holder.placeName.setText(place.getPlaceName());
        holder.placeAddress.setText(place.getAddress());

        holder.time.setVisibility(View.GONE);

        // isEditing 상태에 따라 아이콘의 가시성을 설정
        if (isEditing) {
            holder.menu.setVisibility(View.VISIBLE);
            holder.drag.setVisibility(View.VISIBLE);
        } else {
            holder.menu.setVisibility(View.GONE);
            holder.drag.setVisibility(View.GONE);
        }

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

        // 더보기 아이콘 클릭 시 팝업 메뉴 표시
        holder.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.menu);
            popupMenu.inflate(R.menu.place_edit_item_menu);
            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.menu_find_alternative) {
                    // 대체 장소 찾기 기능 실행
                    Toast.makeText(v.getContext(), "대체 장소 찾기", Toast.LENGTH_SHORT).show();
                    return true;
                } else if(item.getItemId() == R.id.menu_delete) {
                    // 일정 삭제 기능 실행
                    placesList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, placesList.size());
                    Toast.makeText(v.getContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(placesList);
                    }
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= placesList.size() || toPosition < 0 || toPosition >= placesList.size()) {
            return false;
        }

        Collections.swap(placesList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (dataChangeListener != null) {
            dataChangeListener.onDataChanged(placesList);
        }
        return true;
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView placeAddress;
        TextView time;
        ImageView mapIcon;
        ImageView menu, drag;
        View line;


        PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            time = itemView.findViewById(R.id.time);
            mapIcon = itemView.findViewById(R.id.mapIcon);
            menu = itemView.findViewById(R.id.menu);
            drag = itemView.findViewById(R.id.drag);
            line = itemView.findViewById(R.id.line);
        }
    }
}
