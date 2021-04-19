package com.xz.keybag.activity;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.NumberKeyboard;
import com.xz.keybag.fingerprint.FingerprintHelper;
import com.xz.keybag.fingerprint.OnAuthResultListener;
import com.xz.keybag.jni.NativeUtils;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.keybag.utils.AppInfoUtils;
import com.xz.utils.MD5Util;

import butterknife.BindView;

public class LoadActivityV2 extends BaseActivity {

	@BindView(R.id.input_type)
	TextView inputType;
	@BindView(R.id.number_view)
	NumberKeyboard numberView;
	@BindView(R.id.et_pwd)
	TextView etPwd;
	@BindView(R.id.input_layout)
	LinearLayout inputLayout;
	@BindView(R.id.input_layout_2)
	LinearLayout inputLayout2;
	@BindView(R.id.tv_tips)
	TextView tvTips;
	@BindView(R.id.tv_input_tips)
	TextView tvInputTips;

	private int MAX_NUM = 4;
	private FingerprintHelper fingerprintHelper;
	//震动幅度
	private float shakeDegrees = 3f;
	private Vibrator vibrator;
	private int mode;

	@Override
	public boolean homeAsUpEnabled() {
		return false;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_load_v2;
	}

	@Override
	public void initData() {
		Logger.w("签名：" + AppInfoUtils.getPackageSign(this, false));
		Logger.w("sign加密："+ NativeUtils.signatureParams("utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation"));
		if (getIntent() != null) {
			mode = getIntent().getIntExtra("mode", 0);
		}
		new ReadThread().start();
		initView();
		initFingerprint();
		//震动服务
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}


	private void initView() {
		inputLayout.setVisibility(View.GONE);
		inputType.setVisibility(View.VISIBLE);
		inputLayout2.setVisibility(View.VISIBLE);
		numberView.setOnCallBack(new NumberKeyboard.CallBack() {
			@Override
			public void clickNum(String num) {
				playVibration();
				if (etPwd.getText().toString().trim().length() == MAX_NUM - 1) {
					etPwd.append(num);
					checkPwd();
					return;
				} else if (etPwd.getText().toString().trim().length() >= MAX_NUM) {
					return;
				}
				etPwd.append(num);
			}

			@Override
			public void deleteNum() {
				String pwd = etPwd.getText().toString().trim();
				if (pwd.length() == 0)
					return;
				pwd = pwd.substring(0, pwd.length() - 1);
				etPwd.setText(pwd);
			}
		});
		inputType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inputLayout.setVisibility(View.VISIBLE);
			}
		});
	}


	private void initFingerprint() {
		fingerprintHelper = FingerprintHelper.getInstance(mContext);
		fingerprintHelper.setOnAuthResultListener(new OnAuthResultListener() {
			@Override
			public void onSuccess() {
				tvTips.setText("验证成功");
				killMySelf();

			}

			@Override
			public void onHelper(String msg) {
				tvTips.setText(msg);
				playErrState(tvTips);
			}

			@Override
			public void onFailed(String msg) {
				tvTips.setText(msg);
				playErrState(tvTips);
			}

			@Override
			public void onAuthenticationFailed(String msg) {
				tvTips.setText(msg);
				playErrState(tvTips);

			}

			@Override
			public void onDeviceNotSupport() {
				//设备不支持指纹识别
				inputLayout2.setVisibility(View.GONE);
				inputLayout.setVisibility(View.GONE);
				inputLayout.setVisibility(View.VISIBLE);

			}

		});
		//开始监听
		fingerprintHelper.startListening();
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

	private void checkPwd() {
		String temp = etPwd.getText().toString().trim();
		if (temp.equals("")) {
			return;
		}

		temp = MD5Util.getMD5(temp);
		if (temp.equals(Local.User.loginPwd)) {
			killMySelf();
		} else {
			//密码错误
			playErrState(etPwd);
			playErrorVibration();
		}
	}


	/**
	 * 输入错误状态
	 */
	private void playErrState(View v) {
		ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(v, rotateValuesHolder);
		objectAnimator.setDuration(1500);
		objectAnimator.start();
	}

	/**
	 * 数字点击震动效果
	 */
	private void playVibration() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			vibrator.vibrate(VibrationEffect.EFFECT_CLICK);
		} else {
			vibrator.vibrate(30);
		}
	}

	/**
	 * 错误震动
	 */
	private void playErrorVibration() {
		vibrator.vibrate(2000);
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

	@Override
	public void onBackPressed() {
		if (inputLayout.getVisibility() == View.VISIBLE) {
			inputLayout.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		fingerprintHelper.stopListener();
	}


	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();


			Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_ACC);
			//如果游标为空则返回false
			if (!cursor.moveToFirst()) {
				tvInputTips.setText(R.string.string_3);
				Local.User.loginPwd = MD5Util.getMD5(Local.DEFAULT);
				return;
			}

			Local.User.loginPwd = EOD.decrypt(cursor.getString(cursor.getColumnIndex("p2")), Local.SECRET_PWD);
			cursor.close();
		}
	}
}
