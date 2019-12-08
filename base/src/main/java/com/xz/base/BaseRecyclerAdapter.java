package com.xz.base;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mList;
    protected OnItemClickListener<T> mOnItemClickListener;
    private Handler mHandler;

    public BaseRecyclerAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 追加数据
     *
     * @param list
     */
    public void refresh(List<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 先清除原先的集合再添加进去
     *
     * @param list
     */
    public void superRefresh(List<T> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void addItemTop(List<T> datas) {
        mList = datas;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getDatas() {
        return mList;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createNewViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder holder, int position) {
        showViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mList.size() > 0) {
            return mList.size();
        }
        return 0;
    }


    protected abstract void showViewHolder(BaseRecyclerViewHolder holder, int position);

    /***
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType);
}
