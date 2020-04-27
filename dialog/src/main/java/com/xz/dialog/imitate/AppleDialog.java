package com.xz.dialog.imitate;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xz.dialog.R;
import com.xz.dialog.base.BaseDialog;
import com.xz.dialog.event.NegativeOnClickListener;
import com.xz.dialog.event.PositiveOnClickListener;

/**
 * @author czr
 * @date 2020/4/8
 * 仿IOS对话框
 */
public class AppleDialog extends BaseDialog {

    private String title, content;//标题和类容
    private String negativeName, positiveName;//标题和类容
    private NegativeOnClickListener negativeOnClickListener;
    private PositiveOnClickListener positiveOnClickListener;

    private TextView tvTitle;
    private TextView tvContent;
    private Button negative;
    private Button positive;

    public AppleDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_ios;
    }

    @Override
    protected void initView() {

        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        negative = findViewById(R.id.negative);
        positive = findViewById(R.id.positive);
    }

    @Override
    protected void initData() {
        //使用苹果字体
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/pingfangziti.ttf");
        tvTitle.setTypeface(typeface, Typeface.BOLD);
        tvContent.setTypeface(typeface);
        negative.setTypeface(typeface);
        positive.setTypeface(typeface);

        title = "示例";
        content = "Hello world!";
        negativeName = "取消";
        positiveName = "确定";
    }


    @Override
    public void show() {
        super.show();
        refreshView();
    }

    /**
     * 刷新视图
     */
    private void refreshView() {
        tvTitle.setText(title);
        tvContent.setText(content);
        negative.setText(negativeName);
        positive.setText(positiveName);

        if (negativeOnClickListener == null) {
            negative.setVisibility(View.GONE);
            findViewById(R.id.vertical_view).setVisibility(View.GONE);
        } else {
            findViewById(R.id.vertical_view).setVisibility(View.VISIBLE);
            negative.setOnClickListener(onClickListener);
        }
        positive.setOnClickListener(onClickListener);


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.negative) {
                if (negativeOnClickListener != null)
                    negativeOnClickListener.OnClick(v);
            } else if (id == R.id.positive) {
                if (positiveOnClickListener != null)
                    positiveOnClickListener.OnClick(v);
            }
            dismiss();

        }
    };

    public static class Builder {
        AppleDialog dialog;

        public Builder(Context context) {
            dialog = new AppleDialog(context);
            dialog.create();
        }

        /**
         * 设置标题
         *
         * @param title
         * @return
         */
        public Builder setTitle(CharSequence title) {
            dialog.title = title.toString();
            return this;
        }

        /**
         * 设置内容
         *
         * @param content
         * @return
         */
        public Builder setContent(String content) {
            dialog.content = content;
            return this;
        }

        /**
         * 取消按钮监听，不设置不显示
         *
         * @param name
         * @param listener
         * @return
         */
        public Builder setNegativeOnClickListener(CharSequence name, NegativeOnClickListener listener) {
            dialog.negativeOnClickListener = listener;
            dialog.negativeName = name.toString();
            return this;
        }

        /**
         * 确定按钮监听，不设置也会显示2333
         *
         * @param name
         * @param listener
         * @return
         */
        public Builder setPositiveOnClickListener(CharSequence name, PositiveOnClickListener listener) {
            dialog.positiveOnClickListener = listener;
            dialog.positiveName = name.toString();
            return this;
        }

        public AppleDialog create() {
            return dialog;
        }


    }

}
