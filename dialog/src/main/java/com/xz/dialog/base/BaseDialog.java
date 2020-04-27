package com.xz.dialog.base;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.xz.dialog.R;
import com.xz.dialog.utils.SystemUtils;

/**
 * @author czr
 * @date 2020/4/7
 */
public abstract class BaseDialog extends Dialog {

    protected Context mContext;

    public BaseDialog(Context context) {
        this(context, R.style.customDialog);
    }

    private BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        //判断是平板模式还是手机模式
        if (SystemUtils.isPad(mContext)) {
            //1/3屏幕宽度
            lp.width = (int) (dm.widthPixels * 0.4);
            lp.height = (int) (dm.heightPixels * 0.6);

        } else {
            //判断当前是横屏模式还是竖屏模式
            if (SystemUtils.scrennMode(mContext) == Configuration.ORIENTATION_LANDSCAPE) {

                lp.width = (int) (dm.widthPixels * 0.4);
                lp.height = (int) (dm.heightPixels * 0.8);
            } else {
                //宽度始终是屏幕的80%
                lp.width = (int) (dm.widthPixels * widthP);
            }

        }

        lp.dimAmount = dim;//背景暗度
        window.setAttributes(lp);
        initView();
        initData();
    }


    /**
     * 布局资源
     *
     * @return
     */
    protected abstract int getLayoutResource();

    /**
     * 控件初始化
     */
    protected abstract void initView();

    /**
     * 数据初始化
     */
    protected abstract void initData();


    /**
     * 背景明暗度
     */
    private static float dim = 0.2f;

    /**
     * 宽比重 0~1范围
     */
    private static float widthP = 0.8f;

    public static void init(DialogConfig config) {

        dim = config.getDim();
        widthP = config.getWidthP();
    }

    /**
     * 用户自定义初始化类，可配置该模块类所有的Dialog
     * 可以在Application里进行初始化配置
     */
    public final static class DialogConfig {
        /**
         * 背景明暗度
         */
        private float dim;
        /**
         * 宽比重 0~1范围
         */
        private float widthP;

        public DialogConfig() {

        }

        public float getDim() {
            return dim;
        }

        public void setDim(float dim) {
            this.dim = dim;
        }

        public float getWidthP() {
            return widthP;
        }

        public void setWidthP(float width) {
            this.widthP = width;
        }
    }


}
