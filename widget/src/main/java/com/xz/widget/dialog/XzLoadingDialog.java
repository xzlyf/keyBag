package com.xz.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xz.xzwidget.R;

public class XzLoadingDialog extends Dialog {
    private Context mContext;
    private TextView cancel;
    private ImageView loadView;
    private XOnClickListener cancelListener;
    private CharSequence cancelText;
    private Boolean cancelEnable = false;

    public XzLoadingDialog(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawableResource(R.color.transparent);//背景透明
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7);
        //lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.2f;//背景不变暗
        dialogWindow.setAttributes(lp);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
        
    }


    private void initView() {
        cancel = findViewById(R.id.cancel);
        loadView = findViewById(R.id.load_view);
        Glide.with(mContext).load(R.drawable.loading).into(loadView);

    }

    @Override
    public void show() {
        super.show();
        show(this);
    }

    private void show(XzLoadingDialog dialog) {
        if (cancelEnable){
            cancel.setVisibility(View.VISIBLE);
        }else{
            cancel.setVisibility(View.GONE);
        }
        //----------------------------------------------------------
        if (cancelListener != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelListener.onClick(v.getId(), cancelText.toString(), 0);
                    dismiss();
                }
            });
        } else {
            cancel.setVisibility(View.GONE);
        }
        //----------------------------------------------------------
        if (!TextUtils.isEmpty(dialog.cancelText.toString())) {
            cancel.setText(dialog.cancelText);
        } else {
            cancel.setText("取消");

        }

    }


    public static class Builder {
        private XzLoadingDialog dialog;

        public Builder(Context context) {
            dialog = new XzLoadingDialog(context);

        }


        public Builder setCancelEnable(Boolean b) {
            dialog.cancelEnable = b;
            return this;
        }

        public Builder setCancelText(CharSequence text) {
            dialog.cancelText = text;
            return this;
        }

        public Builder setCancelOnClickListener(XOnClickListener listener) {
            dialog.cancelListener = listener;
            return this;
        }


        /**
         * 通过Builder类设置完属性后构造对话框的方法
         */
        public XzLoadingDialog create() {
            return dialog;
        }


    }
}