package com.xz.keybag.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xz.base.BaseRecyclerAdapter;
import com.xz.base.BaseRecyclerViewHolder;
import com.xz.keybag.R;
import com.xz.keybag.entity.KeyEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class KeyAdapter extends BaseRecyclerAdapter<KeyEntity> {


    private List<KeyEntity> filterDatas;

    public KeyAdapter(Context context) {
        super(context);
        this.filterDatas = mList;
    }

    @Override
    public int getItemCount() {
        return filterDatas.size();
    }


    @Override
    protected void showViewHolder(BaseRecyclerViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        KeyEntity entity = filterDatas.get(position);
        viewHolder.name.setText(entity.getT1());
        viewHolder.userId.setText(entity.getT2());
        viewHolder.userPsw.setText(entity.getT3());

    }

    @Override
    protected BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_key, parent, false));
    }


    /**
     * 过滤器哦
     *
     * @return
     */
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    filterDatas = mList;
                } else {
                    List<KeyEntity> filteredList = new ArrayList<>();

                    //toLowerCase统一设置为小写，这样就忽略大小写了

                    for (int i = 0; i < mList.size(); i++) {
                        if (mList.get(i).getT1().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(mList.get(i));
                        }
                    }

                    filterDatas = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterDatas;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterDatas = (List<KeyEntity>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class ViewHolder extends BaseRecyclerViewHolder implements View.OnClickListener {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.user_id)
        TextView userId;
        @BindView(R.id.user_psw)
        TextView userPsw;
        @BindView(R.id.state)
        ImageView state;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getLayoutPosition(), filterDatas.get(getLayoutPosition()));
            }
        }
    }

}
