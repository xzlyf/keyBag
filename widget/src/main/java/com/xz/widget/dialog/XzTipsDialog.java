package com.xz.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xz.xzwidget.R;

public class XzTipsDialog extends Dialog {
    private Context mContext;
    private TextView tvtitle;
    private CharSequence titleText;
    private int titleBackgroundResource = R.color.hot_pink;
    private float titleTextSize = 20;
    private int titleTextColor = Color.parseColor("#FFFFF0");
    private int titleGravity = Gravity.CENTER;
    private CharSequence contentText;
    private TextView tvcontent;
    private int contextBackgroundResour = R.color.white;
    private Button cancel;
    private Button submit;
    private XOnClickListener cancelListener;
    private XOnClickListener submieListener;
    private CharSequence cancelText;
    private CharSequence submieText;
    private int cancelRes = R.drawable.btn_jianbian;
    private int submitRes = R.drawable.btn_jianbian;
    private int cancelTextColor = Color.parseColor("#FFFFFF");
    private int submitTextColor = Color.parseColor("#FFFFFF");

    public XzTipsDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tips);
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);//背景透明
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7);
        //lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.2f;//背景不变暗
        dialogWindow.setAttributes(lp);

        initView();
    }

    private void initView() {
        tvtitle = findViewById(R.id.tvtitle);
        tvcontent = findViewById(R.id.tvcontent);
        cancel = findViewById(R.id.cancel);
        submit = findViewById(R.id.submit);
    }

    @Override
    public void show() {
        super.show();
        //----------标题设置-----------------
        if (!TextUtils.isEmpty(titleText)) {
            tvtitle.setText(titleText);
            tvtitle.setVisibility(View.VISIBLE);
            tvtitle.setTextSize(titleTextSize);
            tvtitle.setBackgroundResource(titleBackgroundResource);
            tvtitle.setTextColor(titleTextColor);
            tvtitle.setGravity(titleGravity);
        } else {
            tvtitle.setVisibility(View.GONE);
        }
        //----------内容设置-----------------

        if (!TextUtils.isEmpty(contentText)) {
            tvcontent.setVisibility(View.VISIBLE);
            tvcontent.setText(contentText);
            tvcontent.setBackgroundResource(contextBackgroundResour);
        } else {
            tvcontent.setVisibility(View.GONE);
        }

        //----------按钮设置-----------------
        if (cancelListener != null) {
            cancel.setVisibility(View.VISIBLE);
            cancel.setText(cancelText);
            cancel.setBackgroundResource(cancelRes);
            cancel.setTextColor(cancelTextColor);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelListener.onClick(v.getId(), "", 0);
                    dismiss();
                }
            });
        } else {
            cancel.setVisibility(View.GONE);
        }
        if (submieListener != null) {
            submit.setVisibility(View.VISIBLE);
            submit.setText(submieText);
            submit.setBackgroundResource(submitRes);
            submit.setTextColor(submitTextColor);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submieListener.onClick(v.getId(), "", 0);
                }
            });
        } else {
            submit.setVisibility(View.GONE);
        }
    }


    public static class Builder {
        private XzTipsDialog dialog;

        public Builder(Context context) {
            dialog = new XzTipsDialog(context);
        }

        /**
         * 设置标题
         * 默认不设置不显示
         *
         */
        public Builder setTitle(CharSequence text) {
            dialog.titleText = text;
            return this;
        }

        /**
         * 设置标题背景资源
         * 可设置透明资源
         *
         */
        public Builder setTitleBackgroundResource(int res) {
            dialog.titleBackgroundResource = res;
            return this;
        }

        /**
         * 设置标题字体大小
         *
         */
        public Builder setTitleTextSize(float f) {
            dialog.titleTextSize = f;
            return this;
        }

        /**
         * 设置标题字体颜色
         *
         */
        public Builder setTitleTextColor(int color) {
            dialog.titleTextColor = color;
            return this;
        }

        /**
         * 设置标题位置
         *
         */
        public Builder setTitleGravity(int gravity) {
            dialog.titleGravity = gravity;
            return this;
        }

        /**
         * 设置内容
         *
         */
        public Builder setContent(CharSequence text) {
            dialog.contentText = text;
            return this;
        }

        /**
         * 设置内容背景
         *
         */
        public Builder setContextBackgroundResour(int res) {
            dialog.contextBackgroundResour = res;
            return this;
        }

        /**
         * 取消点击事件监听
         *
         */
        public Builder setCancelOnclickListener(CharSequence tex, XOnClickListener listener) {
            dialog.cancelText = tex;
            dialog.cancelListener = listener;
            return this;
        }

        /**
         * 提交点击时间监听
         *
         */
        public Builder setSubmitOnClickListener(CharSequence tex, XOnClickListener listener) {
            dialog.submieText = tex;
            dialog.submieListener = listener;
            return this;
        }

        /**
         * 设置按钮背景资源
         *
         */
        public Builder setCancelBtnBackground(int res) {
            dialog.cancelRes = res;
            return this;
        }

        /**
         * 设置按钮背景资源
         *
         */
        public Builder setSubmitBtnBackground(int res) {
            dialog.submitRes = res;
            return this;
        }

        /**
         * 取消按钮字体颜色
         *
         */
        public Builder setCancelTextColor(int color) {
            dialog.cancelTextColor = color;
            return this;
        }

        /**
         * 确定按钮字体颜色
         *
         */
        public Builder setSubmitTextColor(int color) {
            dialog.submitTextColor = color;
            return this;
        }

        public XzTipsDialog create() {
            return dialog;
        }
    }
}
