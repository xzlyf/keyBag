package com.xz.keybag.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.utils.SharedPreferencesUtil;

import butterknife.BindView;

public class SecretActivity extends BaseActivity {

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
        return R.layout.activity_secret;
    }

    @Override
    public void initData() {
        setTitle(R.string.string_15);

        isShow = SharedPreferencesUtil.getBoolean(mContext, "state", "secret_state", false);
        if (isShow) {
            showEye();
        } else {
            hideEye();
        }

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isShow) {
                    isShow = false;
                    hideEye();
                } else {
                    isShow = true;
                    showEye();
                }

            }
        });

    }

    private void showEye() {
        state.setImageResource(R.drawable.ic_show);
        secretText.setText(Local.secret);
    }

    private void hideEye() {
        state.setImageResource(R.drawable.ic_hide);
        secretText.setText("********");
    }


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferencesUtil.saveBoolean(mContext, "state", "secret_state", isShow);
    }
}
