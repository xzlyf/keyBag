package com.xz.widget.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.xz.widget.entity.CommEntity;

import java.util.ArrayList;
import java.util.List;

public class XBanner extends RecyclerView {
    private XBannerAdapter adapter;

    public XBanner(@NonNull Context context) {
        super(context);
        init(context);
    }


    public XBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter = new XBannerAdapter(context);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(manager);
        setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    /**
     * 填装图片
     *
     * @param list
     */
    public void load(List<CommEntity> list) {
        adapter.refresh(list);
    }

    /**
     * 填装图片
     *
     * @param imgUrl
     * @param text
     */
    public void load(String imgUrl, String text) {
        List<CommEntity> list = new ArrayList<>();
        list.add(new CommEntity(imgUrl, text));
        adapter.refresh(list);
    }

}
