package com.tourbus.tourrand;

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

        // isEditing 상태에 따라 아이콘의 가시성을 설정
        if (isEditing) {
            holder.menu.setVisibility(View.VISIBLE);
            holder.drag.setVisibility(View.VISIBLE);
        } else {
            holder.menu.setVisibility(View.GONE);
            holder.drag.setVisibility(View.GONE);
        }

        if (position < placesList.size() - 1) {
            holder.mapIcon.setVisibility(View.VISIBLE);
            holder.mapIcon.setOnClickListener(v -> {
                Context context = v.getContext();
                Place nextPlace = placesList.get(position + 1);

                String apiKey = "856b60d15352dfaae39da72e011fc9c3";  // YOUR_API_KEY를 실제 키로 대체하세요.
                String origin = place.getLatitude() + "," + place.getLongitude();
                String destination = nextPlace.getLatitude() + "," + nextPlace.getLongitude();

                KakaoApiService service = ApiClient.createService();
                service.getDrivingTime(apiKey, origin, destination).enqueue(new Callback<KakaoResponse>() {
                    @Override
                    public void onResponse(Call<KakaoResponse> call, Response<KakaoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int duration = response.body().getRoutes().get(0).getSummary().getDuration();
                            int minutes = duration / 60;
                            holder.time.setText(minutes + "분 소요 예정");
                        } else {
                            holder.time.setText("시간 계산 불가");
                        }
                    }

                    @Override
                    public void onFailure(Call<KakaoResponse> call, Throwable t) {
                        holder.time.setText("오류 발생");
                    }
                });

                String url = "kakaomap://route?sp=" + origin +
                        "&ep=" + destination + "&by=CAR";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);

//                String url = "kakaomap://route?sp=" + "37.53723,127.00551" +
//                        "&ep=" + "37.49795,127.027637&by=CAR";  //PUBLICTRANSIT
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                context.startActivity(intent);
            });
        } else {
            holder.mapIcon.setVisibility(View.GONE);
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
        Collections.swap(placesList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    class PlacesViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView placeAddress;
        TextView time;
        ImageView mapIcon;
        ImageView menu, drag;


        PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            time = itemView.findViewById(R.id.time);
            mapIcon = itemView.findViewById(R.id.mapIcon);
            menu = itemView.findViewById(R.id.menu);
            drag = itemView.findViewById(R.id.drag);
        }
    }
}
