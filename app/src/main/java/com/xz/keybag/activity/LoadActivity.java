package com.xz.keybag.activity;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.fingerprint.FingerprintHelper;
import com.xz.keybag.fingerprint.OnAuthResultListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadActivity extends BaseActivity {

    @BindView(R.id.change_psw)
    TextView changePsw;
    @BindView(R.id.fingerprint_icon)
    ImageView fingerprintIcon;
    @BindView(R.id.tx)
    TextView tx;
    private FingerprintHelper fingerprintHelper;
    private float shakeDegrees = 1f;
    private ObjectAnimator objectAnimator;

    @Override
    public boolean homeAsUpEnabled() {
        return false;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_load;
    }

    @Override
    public void initData() {

        objectAnimator = ObjectAnimator.ofPropertyValuesHolder(tx, rotateValuesHolder);
        objectAnimator.setDuration(1500);

        changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killMySelf();
            }
        });
        fingerprintHelper = FingerprintHelper.getInstance(mContext);
        fingerprintHelper.setOnAuthResultListener(new OnAuthResultListener() {
            @Override
            public void onSuccess() {
                killMySelf();
            }

            @Override
            public void onHelper(String msg) {
                tx.setText(msg);
                objectAnimator.start();
            }

            @Override
            public void onFailed(String msg) {
                tx.setText(msg);
                objectAnimator.start();

            }

            @Override
            public void onAuthenticationFailed(String msg) {
                tx.setText(msg);
                objectAnimator.start();

            }

            @Override
            public void onDeviceNotSupport() {
                tx.setText("设备不支持指纹识别\n请使用密码校验");
            }

        });
        //开始监听
        fingerprintHelper.startListening();
    }

    private void killMySelf() {
        startActivity(new Intent(mContext, MainActivity.class));
        overridePendingTransition(R.anim.translation_finish, R.anim.translation_create);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //一秒后销毁活动
                finish();
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fingerprintHelper.stopListener();
    }


    /**
     * 字体左右晃动动画
     */
    private PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
            Keyframe.ofFloat(0f, 0f),
            Keyframe.ofFloat(0.1f, -shakeDegrees),
            Keyframe.ofFloat(0.2f, shakeDegrees),
            Keyframe.ofFloat(0.3f, -shakeDegrees),
            Keyframe.ofFloat(0.4f, shakeDegrees),
            Keyframe.ofFloat(0.5f, -shakeDegrees),
            Keyframe.ofFloat(0.6f, shakeDegrees),
            Keyframe.ofFloat(0.7f, -shakeDegrees),
            Keyframe.ofFloat(0.8f, shakeDegrees),
            Keyframe.ofFloat(0.9f, -shakeDegrees),
            Keyframe.ofFloat(1.0f, 0f)
    );

}
