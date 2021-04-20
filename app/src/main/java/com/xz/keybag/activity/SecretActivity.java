package com.xz.keybag.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.RandomString;
import com.xz.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecretActivity extends BaseActivity {

    @BindView(R.id.secret_text)
    TextView secretText;
    @BindView(R.id.state)
    ImageView state;
    @BindView(R.id.change_secret)
    TextView changeSecret;
    @BindView(R.id.layout_1)
    ConstraintLayout layout1;
    @BindView(R.id.is_check)
    CheckBox isCheck;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.tv_submit)
    Button tvSubmit;
    @BindView(R.id.layout_2)
    LinearLayout layout2;
    private boolean isShow;
    private boolean isChange = false;

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
        //默认显示
        setTitle(R.string.string_15);
        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);


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
        changeSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange) {
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                    isChange = false;
                } else {
                    layout2.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    isChange = true;
                }
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheck.isChecked()) {
                    sToast("请阅读后勾选“已阅读”");
                    return;
                }

                String st = etInput.getText().toString().trim();
                if (st.equals("")) {
                    sToast("无效密钥");
                    return;
                }
                if (st.length()!=8){
                    sToast("密钥固定长度为8位");
                    return;
                }
                //更新密钥
                ContentValues values = new ContentValues();
                values.put("k1", EOD.encrypt(st, Local.SECRET_KEY));
                values.put("k2", RandomString.getRandomString(16));
                values.put("k3", 0);
                SqlManager.update(mContext, "secret", values, "k1 = ?", new String[]{EOD.encrypt(Local.secret, Local.SECRET_KEY)});
                sToast("密钥已修改，重启生效");
                Local.secret = st;
                etInput.setText("");
                isCheck.setChecked(false);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
