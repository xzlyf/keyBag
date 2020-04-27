package com.xz.dialog.imitate;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xz.dialog.R;
import com.xz.dialog.base.BaseDialog;
import com.xz.dialog.constant.DialogStyle;
import com.xz.dialog.event.NegativeOnClickListener;
import com.xz.dialog.event.PositiveOnClickListener;

/**
 * @author czr
 * @date 2020/4/7
 * <p>
 * 仿原生对话框
 */
public class IconDialog extends BaseDialog implements View.OnClickListener {


    private int backgroundDrawableRes;//背景样式
    private int mainColor;//主色调
    private int type;//图标类型
    private String content;//内容
    private NegativeOnClickListener negativeOnClickListener;//消极的点击
    private PositiveOnClickListener positiveOnClickListener;//积极的点击
    private CharSequence negativeName;//按钮名称
    private CharSequence positiveName;


    private LinearLayout rootView;
    private ImageView iconType;
    private TextView tvMsg;
    private TextView tvNegative;
    private TextView tvPositive;

    public IconDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_icon;
    }

    @Override
    protected void initView() {
        iconType = findViewById(R.id.icon_type);
        tvMsg = findViewById(R.id.tv_msg);
        tvNegative = findViewById(R.id.tv_negative);
        tvPositive = findViewById(R.id.tv_positive);
        rootView = findViewById(R.id.root_layout);

    }

    @Override
    protected void initData() {
        //默认配置
        mainColor = mContext.getResources().getColor(R.color.defaultMainColor);
        type = DialogStyle.TYPE_ICON_TIPS;
        content = "This is Test content,you can use \"setText()\" to replace!";
        negativeName = "DENY";
        positiveName = "ALLOW";
        backgroundDrawableRes = DialogStyle.BG_ANGLE_NONE;

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
        rootView.setBackground(mContext.getDrawable(backgroundDrawableRes));
        iconType.setColorFilter(mainColor);
        tvNegative.setTextColor(mainColor);
        tvPositive.setTextColor(mainColor);
        tvNegative.setText(negativeName);
        tvPositive.setText(positiveName);

        iconType.setImageResource(type);
        tvMsg.setText(content);
        tvNegative.setOnClickListener(this);
        tvPositive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_negative) {
            if (negativeOnClickListener != null)
                negativeOnClickListener.OnClick(v);
        } else if (id == R.id.tv_positive) {
            if (positiveOnClickListener != null)
                positiveOnClickListener.OnClick(v);
        }

        dismiss();
    }


    public static class Builder {
        private IconDialog dialog;

        public Builder(Context context) {
            dialog = new IconDialog(context);
            dialog.create();
        }

        /**
         * 设置内容
         *
         * @param txt
         * @return
         */
        public Builder setContent(String txt) {
            dialog.content = txt;
            return this;
        }

        /**
         * 设置图标
         * 可在常量类使用内置图标 DialogStyle.java
         * 也可以使用自定义图标
         *
         * @param type
         * @return
         */
        public Builder setIcon(int type) {
            dialog.type = type;
            return this;
        }

        /**
         * 设置主色调
         *
         * @param color
         * @return
         */
        public Builder setMainColor(int color) {
            dialog.mainColor = color;
            return this;
        }


        /**
         * 设置圆角角度
         * 默认NONE
         * <p>
         * 可在常量类使用内置样式 DialogStyle.java
         *
         * @param drawableRes 可以自定义指定drawable资源文件，也可以使用自定义的
         * @return
         */
        public Builder setAngle(int drawableRes) {
            dialog.backgroundDrawableRes = drawableRes;
            return this;
        }

        /**
         * negative按钮监听
         *
         * @param name     按钮名称
         * @param listener 监听
         * @return
         */
        public Builder setNegativeOnClickListener(CharSequence name, NegativeOnClickListener listener) {
            dialog.negativeName = name;
            dialog.negativeOnClickListener = listener;
            return this;
        }

        /**
         * positive按钮监听
         *
         * @param name     按钮名称
         * @param listener 监听
         * @return
         */
        public Builder setPositiveOnClickListener(CharSequence name, PositiveOnClickListener listener) {
            dialog.positiveName = name;
            dialog.positiveOnClickListener = listener;
            return this;
        }

        public IconDialog create() {
            return dialog;
        }
    }
}
