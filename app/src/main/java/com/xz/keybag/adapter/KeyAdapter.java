package com.xz.keybag.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.orhanobut.logger.Logger;
import com.xz.base.BaseRecyclerAdapter;
import com.xz.base.BaseRecyclerViewHolder;
import com.xz.keybag.R;
import com.xz.keybag.entity.KeyEntity;
import com.xz.utils.CopyUtil;
import com.xz.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class KeyAdapter extends BaseRecyclerAdapter<KeyEntity> {


    private List<KeyEntity> filterDatas;
    private CopyUtil copyUtil;

    public KeyAdapter(Context context) {
        super(context);
        this.filterDatas = mList;
        copyUtil = new CopyUtil(context);
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
        viewHolder.endTime.setText(TimeUtil.getSimMilliDate("yyyy.MM.dd HH:mm", Long.valueOf(entity.getT5())));

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


    class ViewHolder extends BaseRecyclerViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.user_id)
        TextView userId;
        @BindView(R.id.user_psw)
        TextView userPsw;
        @BindView(R.id.state)
        ImageView state;
        @BindView(R.id.end_time)
        TextView endTime;
        @BindView(R.id.root_layout)
        ConstraintLayout rootLayout;
        @BindView(R.id.layout_2)
        FrameLayout layout2;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLayout.setOnClickListener(this);
            rootLayout.setOnLongClickListener(this);
            userId.setOnClickListener(this);
            userPsw.setOnClickListener(this);



        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.root_layout:
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, getLayoutPosition(), filterDatas.get(getLayoutPosition()));
                    }
                    break;
                case R.id.user_id:
                    copyUtil.copyToClicp(userId.getText().toString());
                    Toast.makeText(mContext, "已复制账号", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.user_psw:
                    copyUtil.copyToClicp(userPsw.getText().toString());
                    Toast.makeText(mContext, "已复制密码", Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            ViewGroup viewGroup = (ViewGroup) v;
            int childWidth = viewGroup.getChildAt(1).getWidth();
            rootLayout.offsetLeftAndRight(-childWidth);
            layout2.offsetLeftAndRight(-childWidth);
            return true;
        }
    }

}
