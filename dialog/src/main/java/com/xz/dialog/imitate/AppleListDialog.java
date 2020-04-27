package com.xz.dialog.imitate;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xz.dialog.R;
import com.xz.dialog.base.BaseDialog;
import com.xz.dialog.event.NegativeOnClickListener;
import com.xz.dialog.event.OnItemClickListener;
import com.xz.dialog.event.PositiveOnClickListener;

/**
 * @author czr
 * @date 2020/4/8
 * 仿IOS对话框
 */
public class AppleListDialog extends BaseDialog {

    private String title, content;//标题和类容
    private String[] items;
    private ArrayAdapter<String> adapter;
    private OnItemClickListener<String[]> mListener;

    private TextView tvTitle;
    private TextView tvContent;
    private ListView listView;

    public AppleListDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_ios_list;
    }

    @Override
    protected void initView() {

        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        listView = findViewById(R.id.item_list);
    }

    @Override
    protected void initData() {
        //使用苹果字体
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/pingfangziti.ttf");
        tvTitle.setTypeface(typeface, Typeface.BOLD);
        tvContent.setTypeface(typeface);

        title = "示例";
        content = "是否为我打分~";
        items = new String[]{"好评", "超好评", "无敌好评"};


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
        if (content.equals("")) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setText(content);
        }
        adapter = new ArrayAdapter<>(mContext, R.layout.item_ios, R.id.text1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null)
                    mListener.OnClick(items, position);
                dismiss();

            }
        });
    }


    public static class Builder {
        AppleListDialog dialog;

        public Builder(Context context) {
            dialog = new AppleListDialog(context);
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
         * 设置列表
         *
         * @param item
         * @return
         */
        public Builder setItem(String[] item, OnItemClickListener<String[]> listener) {
            dialog.items = item;
            dialog.mListener = listener;
            return this;
        }


        public AppleListDialog create() {
            return dialog;
        }


    }

}
