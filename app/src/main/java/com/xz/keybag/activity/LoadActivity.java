package com.xz.keybag.activity;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.keybag.fingerprint.FingerprintHelper;
import com.xz.keybag.fingerprint.OnAuthResultListener;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.KeyBoardUtil;
import com.xz.utils.MD5Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LoadActivity extends BaseActivity {

    @BindView(R.id.change_psw)
    TextView changePsw;
    @BindView(R.id.fingerprint_icon)
    ImageView fg_icon;
    @BindView(R.id.tx)
    TextView tx;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.submit)
    ImageView submit;
    @BindView(R.id.psw_tips)
    TextView pwdTips;
    private FingerprintHelper fingerprintHelper;
    private float shakeDegrees = 3f;
    private ObjectAnimator objectAnimator;
    private ObjectAnimator objectAnimator2;
    private int mode;
    private final int SEND_HINT = 1001;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == SEND_HINT) {
                pwdTips.setText((String)msg.obj);
            }
        }
    };
    private ShortcutManager mShortcutManager;

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
        if (getIntent() != null) {
            mode = getIntent().getIntExtra("mode", 0);
        }
        new ReadThread().start();
        etInput.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        pwdTips.setVisibility(View.GONE);

        objectAnimator = ObjectAnimator.ofPropertyValuesHolder(tx, rotateValuesHolder);
        objectAnimator.setDuration(1500);
        objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(pwdTips, rotateValuesHolder);
        objectAnimator2.setDuration(1500);

        changePsw.setOnClickListener(stateOnClickListener);
        submit.setOnClickListener(check);
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
                tx.setText(R.string.string_4);
                fg_icon.setVisibility(View.GONE);
                changePwdState();
            }

        });
        //开始监听
        fingerprintHelper.startListening();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupShortcuts();
        }
    }

    /**
     * 加入桌面长按快捷进入
     * Shortcuts 动态添加
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupShortcuts() {
        mShortcutManager = getSystemService(ShortcutManager.class);

        List<ShortcutInfo> infos = new ArrayList<>();
            Intent intent = new Intent(this, AddActivity.class);
            intent.setAction(Intent.ACTION_VIEW);

            ShortcutInfo info = new ShortcutInfo.Builder(this, "id" )
                    .setShortLabel("新增")
                    .setLongLabel("新增记项")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_add_black))
                    .setIntent(intent)
                    .build();
            infos.add(info);
//            manager.addDynamicShortcuts(Arrays.asList(info));

        mShortcutManager.setDynamicShortcuts(infos);
    }

    private void killMySelf() {
        //判断模式，打开对应的活动
        if (mode == 1) {
            startActivity(new Intent(mContext, KeyActivity.class));
            finish();
            return;
        }
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
     * 切换至密码输入状态
     */
    private void changePwdState() {
        changePsw.setVisibility(View.GONE);
        etInput.setVisibility(View.VISIBLE);
        submit.setVisibility(View.VISIBLE);
        pwdTips.setVisibility(View.VISIBLE);
        etInput.requestFocus();
        KeyBoardUtil.showKeyBoard(etInput);
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

    /**
     * 切换至密码输入状态
     */
    private View.OnClickListener stateOnClickListener = v -> changePwdState();

    /**
     * 检查密码
     */
    private View.OnClickListener check = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String temp = etInput.getText().toString().trim();
            if (temp.equals("")) {
                return;
            }

            temp = MD5Util.getMD5(temp);
            if (temp.equals(Local.User.loginPwd)) {
                killMySelf();
            } else {
                pwdTips.setText(R.string.string_5);
                objectAnimator2.start();
            }

        }
    };

    /**
     * 主线程执行输出提示语句
     *
     * @param msg
     */
    private void setHint(String msg) {
        Message message = handler.obtainMessage();
        message.what = SEND_HINT;
        message.obj = msg;
        handler.sendMessage(message);
    }


    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();


            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_ACC);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                setHint(getString(R.string.string_3));
                Local.User.loginPwd = MD5Util.getMD5(Local.DEFAULT);
                return;
            }

            Local.User.loginPwd = EOD.decrypt(cursor.getString(cursor.getColumnIndex("p2")), Local.SECRET_PWD);
            cursor.close();
        }
    }

}
