package com.xz.widget.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xz.xzwidget.R;
import com.xz.widget.entity.CommEntity;

import java.util.ArrayList;
import java.util.List;

public class XBannerAdapter extends RecyclerView.Adapter<XBannerAdapter.ViewHolder> {
    private Context mContext;
    private List<CommEntity> mlist;

    public XBannerAdapter(Context context) {
        mContext = context;
        mlist = new ArrayList<>();
    }

    public void refresh(List<CommEntity> list) {
        mlist.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext).load(mlist.get(position%mlist.size()).getS1()).into(holder.img);
        holder.text.setText(mlist.get(position%mlist.size()).getS2());
    }

    @Override
    public int getItemCount() {
        //return mlist.size();
        return Integer.MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.banner_img);
            text = itemView.findViewById(R.id.banner_text);
        }
    }
}
