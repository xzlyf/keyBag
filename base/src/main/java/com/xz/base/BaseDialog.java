package com.xz.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.xz.widget.dialog.XzInputDialog;

public abstract class BaseDialog extends Dialog {
    private Context mContext;
    private OnCancelListener cancelListener;

    public BaseDialog(Context context) {
        this(context, 0);
    }

    public BaseDialog(Context context, int themeResId) {
        this(context, false, null);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        if (cancelListener != null) {
            this.cancelListener = cancelListener;
        }

        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        Window window = getWindow();
        assert window != null;
        window.setBackgroundDrawableResource(com.xz.xzwidget.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        lp.width = (int) (dm.widthPixels * 0.8);
        lp.dimAmount = 0.2f;
        window.setAttributes(lp);

        initData();
    }


    /**
     * 布局资源
     *
     * @return
     */
    protected abstract int getLayoutResource();

    /**
     * 数据初始化
     */
    protected abstract void initData();

}
