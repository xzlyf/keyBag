package com.xz.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    public BaseRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}
