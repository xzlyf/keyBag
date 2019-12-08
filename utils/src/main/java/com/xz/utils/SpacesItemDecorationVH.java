package com.xz.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 修饰RecyclerView的Item的间距
 * 垂直间距
 */
public class SpacesItemDecorationVH extends RecyclerView.ItemDecoration {
    int space;
    public SpacesItemDecorationVH(int space){
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;
        outRect.bottom = space;
        outRect.left = space;
        outRect.right = space;

    }
}
