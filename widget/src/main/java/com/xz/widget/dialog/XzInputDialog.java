package com.xz.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xz.xzwidget.R;


public class XzInputDialog extends Dialog {
    private Context mContext;
    private LinearLayout linearLayout;
    private TextView tvTitle;
    private EditText etText;
    private Button submit;
    private ImageView tvClose;
    private int minLine = 3;
    private int maxLine = 3;
    private CharSequence hint = "在此输入内容啦";
    private CharSequence title = "";
    private XOnClickListener submitListener;
    private CharSequence submitText;
    private boolean showClose;


    public XzInputDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input);
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        lp.width = (int) (dm.widthPixels * 0.8);
        lp.dimAmount = 0.2f;
        window.setAttributes(lp);
        initView();
    }

    private void initView() {
        linearLayout = findViewById(R.id.linearLayout);
        tvTitle = findViewById(R.id.tv_title);
        etText = findViewById(R.id.et_text);
        submit = findViewById(R.id.submit);
        tvClose = findViewById(R.id.tv_close);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @Override
    public void show() {
        super.show();
        if (showClose){
            tvClose.setVisibility(View.VISIBLE);
        }else{
            tvClose.setVisibility(View.GONE);
        }
        etText.setMinLines(minLine);
        etText.setMaxLines(maxLine);
        etText.setHint(hint);
        if (title.toString().equals("")) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        if (submitListener != null) {
            submit.setText(submitText);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitListener.onClick(v.getId(), etText.getText().toString(), 0);
                    dismiss();
                }
            });
        }
    }


    public static class Builder {
        XzInputDialog dialog;

        public Builder(Context context) {
            this.dialog = new XzInputDialog(context);
        }

        /**
         * 设置编辑框最小行数
         *
         * @param i
         * @return
         */
        public Builder setMinLine(int i) {
            dialog.minLine = i;
            return this;
        }

        /**
         * 设置编辑框最大行数
         *
         * @param i
         * @return
         */
        public Builder setMaxLine(int i) {
            dialog.maxLine = i;
            return this;
        }

        /**
         * 设置提示语句
         *
         * @param charSequence
         * @return
         */
        public Builder setHint(CharSequence charSequence) {
            dialog.hint = charSequence;
            return this;
        }

        /**
         * 设置标题
         * 空为不显示
         *
         * @param charSequence
         * @return
         */
        public Builder setTitle(CharSequence charSequence) {
            dialog.title = charSequence;
            return this;
        }

        /**
         * 提交按钮监听并设置按钮文本
         * 可获取输入内容
         *
         * @param listener
         * @return
         */
        public Builder setSubmitOnClickListener(CharSequence tex, XOnClickListener listener) {
            dialog.submitText = tex;
            dialog.submitListener = listener;
            return this;
        }

        /**
         * 设置是否显示右上角的小圆点
         *
         * @param b
         * @return
         */
        public Builder showClose(boolean b) {
            dialog.showClose = b;
            return this;
        }


        public XzInputDialog create() {
            return dialog;
        }
    }
}
