package com.xz.keybag.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseRecyclerAdapter;
import com.xz.keybag.base.BaseRecyclerViewHolder;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.KeyEntity;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
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
        long time;
        try {
            time = Long.valueOf(entity.getT5());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            time = 0;
        }
        viewHolder.endTime.setText(TimeUtil.getSimMilliDate("yyyy.MM.dd HH:mm", time));


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
        LinearLayout rootLayout;
        @BindView(R.id.layout_2)
        FrameLayout layout2;
        @BindView(R.id.layout_1)
        ConstraintLayout layout1;
        @BindView(R.id.delete)
        ImageView delete;
        private ViewGroup viewGroup;
        private int childWidth;
        private boolean isOpen = false;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout1.setOnClickListener(this);
            layout1.setOnLongClickListener(this);
            userId.setOnClickListener(this);
            userPsw.setOnClickListener(this);
            delete.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_1:
                    if (isOpen) {
                        isOpen = false;
                        layout1.offsetLeftAndRight(childWidth);
                        layout2.offsetLeftAndRight(childWidth);
                        return;
                    }
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, getLayoutPosition(), filterDatas.get(getLayoutPosition()));
                    }
                    break;
                case R.id.user_id:
                    copyUtil.copyToClicp(userId.getText().toString());
                    Toast.makeText(mContext, getString(R.string.string_6), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.user_psw:
                    copyUtil.copyToClicp(userPsw.getText().toString());
                    Toast.makeText(mContext, getString(R.string.string_7), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.delete:
                    int position = getLayoutPosition();
                    String t1 = EOD.encrypt(mList.get(position).getT1(), Local.secret);
                    String t2 = EOD.encrypt(mList.get(position).getT2(), Local.secret);
                    String t3 = EOD.encrypt(mList.get(position).getT3(), Local.secret);
                    SqlManager.delete(mContext, Local.TABLE_COMMON, "t1=? and t2=? and t3 = ?", new String[]{t1, t2, t3});
                    mList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, getString(R.string.string_8), Toast.LENGTH_SHORT).show();
                    break;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (viewGroup == null) {
                viewGroup = rootLayout;
                childWidth = viewGroup.getChildAt(1).getWidth();
            }
            if (isOpen) {
                isOpen = false;
                layout1.offsetLeftAndRight(childWidth);
                layout2.offsetLeftAndRight(childWidth);
                return true;
            } else {
                isOpen = true;
                layout1.offsetLeftAndRight(-childWidth);
                layout2.offsetLeftAndRight(-childWidth);
            }
            return true;

        }
    }

}
