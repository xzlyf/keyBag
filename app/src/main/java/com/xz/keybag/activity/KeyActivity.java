package com.xz.keybag.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.fingerprint.FingerprintHelper;
import com.xz.keybag.fingerprint.OnAuthResultListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KeyActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.fingerprint_icon)
    ImageView tv_icon;
    @BindView(R.id.fingerprint_helper)
    TextView tv_info;
    @BindView(R.id.change_psw)
    TextView changePsw;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.submit)
    ImageView submit;

    private FingerprintHelper fingerprintHelper;

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
        setTitle("密码登录");
        initView();
        fingerprintHelper = FingerprintHelper.getInstance(mContext);
        fingerprintHelper.setOnAuthResultListener(new OnAuthResultListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                tv_info.setText("验证成功");

            }

            @Override
            public void onHelper(String msg) {
                Log.d(TAG, "onHelper: " + msg);
                tv_info.setText(msg);
            }

            @Override
            public void onFailed(String msg) {
                Log.d(TAG, "onFailed: " + msg);
                tv_info.setText(msg);
            }

            @Override
            public void onAuthenticationFailed(String msg) {
                Log.d(TAG, "onAuthenticationFailed: " + msg);
                tv_info.setText(msg);

            }

            @Override
            public void onDeviceNotSupport() {
                Log.d(TAG, "onDeviceNotSupport: ");
            }
        });
        fingerprintHelper.startListening();


    }

    private void initView() {
        changePsw.setOnClickListener(this);
        submit.setOnClickListener(this);

        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>0){
                    submit.setVisibility(View.VISIBLE);
                }else{
                    submit.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fingerprintHelper.stopListener();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_psw:
                etPwd.setVisibility(View.VISIBLE);
                break;

            case R.id.submit:

                check();

                break;
        }
    }

    /**
     * 检查验证
     */
    private void check() {
        String tx = etPwd.getText().toString().trim();
        if (tx.equals("")) {
            tv_info.setText("密码不可为空");
            return;
        }



    }
}
