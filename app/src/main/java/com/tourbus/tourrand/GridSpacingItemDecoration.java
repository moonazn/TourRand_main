package com.tourbus.tourrand;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount; // 열의 개수
    private int spacing; // 간격 크기
    private boolean includeEdge; // 가장자리에 간격을 포함할지 여부

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // 아이템의 위치
        int column = position % spanCount; // 열의 위치

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) { // 첫 번째 줄
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // 아래쪽 간격
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing; // 위쪽 간격
            }
        }
    }
}
