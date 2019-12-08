package com.xz.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xz.base.utils.ToastUtil;

public abstract class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        initView(rootView);
        initDate(mContext);
        return rootView;
    }


    /**
     * 初始化布局
     * @return
     */
    protected abstract int getLayout();

    /**
     * 初始化控件
     * @param rootView
     */
    protected abstract void initView(View rootView);

    /**
     * 初始化数据-绑定数据等
     * @param mContext
     */
    protected abstract void initDate(Context mContext);

    public void sToast(String text) {
        ToastUtil.Shows(mContext, text);
    }

    public void lToast(String text) {
        ToastUtil.Shows_LONG(mContext, text);
    }

}
