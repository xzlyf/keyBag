package com.xz.keybag.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xz.base.BaseRecyclerAdapter;
import com.xz.base.BaseRecyclerViewHolder;
import com.xz.keybag.R;
import com.xz.keybag.entity.KeyEntity;

import butterknife.BindView;

public class KeyAdapter extends BaseRecyclerAdapter<KeyEntity> {


    public KeyAdapter(Context context) {
        super(context);
    }

    @Override
    protected void showViewHolder(BaseRecyclerViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        KeyEntity entity = mList.get(position);
        viewHolder.name.setText(entity.getT1());
        viewHolder.userId.setText(entity.getT2());
        viewHolder.userPsw.setText(entity.getT3());
    }

    @Override
    protected BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_key, parent, false));
    }

    class ViewHolder extends BaseRecyclerViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.user_id)
        TextView userId;
        @BindView(R.id.user_psw)
        TextView userPsw;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
