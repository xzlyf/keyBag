package com.xz.keybag.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeyActivity extends BaseActivity {

    @BindView(R.id.secret_text)
    TextView secretText;
    @BindView(R.id.state)
    ImageView state;
    private boolean isShow;

    @Override
    public boolean homeAsUpEnabled() {
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_key;
    }

    @Override
    public void initData() {
        setTitle("密钥管理");

        isShow = SharedPreferencesUtil.getBoolean(mContext, "state", "secret_state", false);
        if (isShow) {
            state.setImageResource(R.drawable.ic_show);
            secretText.setText(Local.secret);

        } else {
            state.setImageResource(R.drawable.ic_hide);
            secretText.setText("********");

        }

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isShow) {
                    isShow = false;
                    state.setImageResource(R.drawable.ic_hide);
                    secretText.setText("********");

                } else {
                    isShow = true;
                    state.setImageResource(R.drawable.ic_show);
                    secretText.setText(Local.secret);

                }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesUtil.saveBoolean(mContext, "state", "secret_state", isShow);
    }
}
