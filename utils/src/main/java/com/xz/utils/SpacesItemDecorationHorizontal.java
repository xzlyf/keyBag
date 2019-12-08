package com.xz.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 修饰RecyclerView的Item的间距
 * 左右间距
 */
public class SpacesItemDecorationHorizontal extends RecyclerView.ItemDecoration {
    int space;
    public SpacesItemDecorationHorizontal(int space){
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;

        if (parent.getChildPosition(view)==0){
            outRect.left = space;
        }
    }
}
