package com.xz.keybag.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.SlidingVerification;
import com.xz.keybag.sql.DBManager;
import com.xz.keybag.utils.PermissionsUtils;
import com.xz.keybag.utils.lock.DES;
import com.xz.keybag.utils.lock.RSA;
import com.xz.keybag.zxing.activity.CaptureActivity;
import com.xz.utils.TimeUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyActivity extends BaseActivity {

	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.open_camera)
	LinearLayout openCamera;
	@BindView(R.id.current_secret)
	TextView currentSecret;
	@BindView(R.id.layout_new_secret)
	LinearLayout newSecretLayout;
	@BindView(R.id.new_secret)
	TextView newSecret;
	@BindView(R.id.tv_log)
	TextView tvLog;
	@BindView(R.id.tv_close)
	TextView tvClose;
	@BindView(R.id.verify_progress)
	SlidingVerification mVerifyProgress;
	@BindView(R.id.tv_top)
	TextView mTvTop;

	private boolean isSafeSecret = false;//是否合法密钥
	private String xtSecret;//接收来的des明文密钥
	private DBManager db;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_modify;
	}

	@Override
	public void initData() {
		newSecretLayout.setVisibility(View.GONE);
		String qrSt = getIntent().getStringExtra(Local.INTENT_EXTRA_QR_CODE);
		if (TextUtils.isEmpty(qrSt)) {
			finish();
			return;
		}
		db = DBManager.getInstance(this);
		currentSecret.setText(verifySecret(qrSt));
		tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
		mVerifyProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				if (seekBar.getProgress() >= seekBar.getMax()) {
					seekBar.setThumb(mContext.getResources().getDrawable(R.mipmap.check_pass_bule));
					seekBar.setThumbOffset(seekBar.getMax() + 50);
					seekBar.setProgress(seekBar.getMax());
					seekBar.setEnabled(false);
					mTvTop.setVisibility(View.VISIBLE);
					//开始修改
					startChange();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				if (seekBar.getProgress() >= seekBar.getMax()) {
					seekBar.setThumb(mContext.getResources().getDrawable(R.mipmap.seekbar_thumb));
					seekBar.setThumbOffset(0);
					seekBar.setProgress(0);
				} else {
					seekBar.setProgress(0);
				}
			}
		});
	}

	@OnClick({R.id.tv_back, R.id.open_camera, R.id.tv_close, R.id.image_camera})
	public void onViewClick(View v) {
		switch (v.getId()) {
			case R.id.image_camera:
			case R.id.open_camera:
				startQrCode();
				break;
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_close:
				newSecretLayout.setVisibility(View.GONE);
				newSecret.setText("");
				xtSecret = null;
				break;
		}
	}


	/**
	 * 调用zxing扫描二维码
	 */
	private void startQrCode() {
		PermissionsUtils.getInstance().chekPermissions(this,
				new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
				new PermissionsUtils.IPermissionsResult() {
					@Override
					public void passPermissons() {
						// 二维码扫码
						Intent intent = new Intent(ModifyActivity.this, CaptureActivity.class);
						startActivityForResult(intent, Local.REQ_QR_CODE);
					}

					@Override
					public void forbitPermissons() {
						AlertDialog finallyDialog = new AlertDialog.Builder(ModifyActivity.this)
								.setMessage("App需要此权限,\n否则无法打开相机扫描二维码")
								.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								})
								.create();
						finallyDialog.show();

					}
				});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//就多一个参数this
		PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//扫描结果回调
		if (requestCode == Local.REQ_QR_CODE && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (bundle == null) {
				return;
			}
			String scanResult = bundle.getString(Local.INTENT_EXTRA_KEY_QR_SCAN);
			if (TextUtils.isEmpty(scanResult)) {
				return;
			}
			handleQrCode(scanResult);
		}
	}


	/**
	 * 处理接收过来的二维码数据
	 */
	private void handleQrCode(String qrSt) {
		//得到加密DES密钥
		String secret = verifySecret(qrSt);
		if (secret.equals("no_message") || secret.equals("error_code")) {
			return;
		}

		newSecretLayout.setVisibility(View.VISIBLE);
		newSecret.setText(secret);
		if (mVerifyProgress.getProgress() >= 99) {
			mVerifyProgress.setProgress(0);
			mVerifyProgress.setThumb(mContext.getResources().getDrawable(R.mipmap.seekbar_thumb));
			mVerifyProgress.setThumbOffset(0);
			mVerifyProgress.setEnabled(true);
			mTvTop.setVisibility(View.INVISIBLE);
		}
		try {
			//得到明文DES
			xtSecret = DES.decryptor(secret, Local.desKey);
		} catch (Exception e) {
			e.printStackTrace();
			xtSecret = null;
		}

	}

	/**
	 * 验证密钥文本
	 * 格式：RSA(头协议@DES(secret))
	 */
	private String verifySecret(String secret) {
		//解密RSA
		try {
			secret = RSA.privateDecrypt(secret, RSA.getPrivateKey(Local.privateKey));
		} catch (Exception e) {
			e.printStackTrace();
			sToast("这个二维码没有我想要的信息(ˉ▽ˉ；)...");
			return "no_message";
		}
		//二维码报文头判断 keybag_secret@RSA密文
		String[] qrArray = secret.split(Local.PROTOCOL_SPLIT);
		if (qrArray.length != 2) {
			sToast("不支持此协议");
			return "no_message";
		}

		if (!qrArray[0].equals(Local.PROTOCOL_QR_SECRET)) {
			sToast("非法二维码");
			return "error_code";
		}

		return qrArray[1];
	}


	/**
	 * 开始修改密钥
	 */
	private void startChange() {
		tvBack.setEnabled(false);
		openCamera.setEnabled(false);
		tvClose.setEnabled(false);
		clearLog();
		appendLog("----开始修改密钥----");
		//appendLog(TimeUtil.getSimMilliDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
		appendLog("请勿退出或关闭");
		if (xtSecret == null) {
			appendLog("密钥无效");
			appendLog("----结束修改密钥----");
			return;
		}
		appendLog("正在测试密钥兼容性...");
		boolean isOK = db.testDES(xtSecret);
		if (!isOK) {
			appendLog("密钥可能不兼容");
		}
		appendLog("正在替换旧密钥...");
		int state = db.updateDES(Local.mAdmin.getDes(), xtSecret);
		appendLog("更新状态：" + state);
		if (state != 1) {
			appendLog("修改密钥失败");
			appendLog("----结束修改密钥----");
			return;
		}
		appendLog("修改密钥成功");
		appendLog(TimeUtil.getSimMilliDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
		appendLog("----结束修改密钥----");

		openCamera.setEnabled(true);
		tvClose.setEnabled(true);
		tvBack.setEnabled(true);

		//显示新的密钥
		currentSecret.setText(newSecret.getText().toString().trim());

	}

	private void appendLog(String st) {
		//if (Looper.myLooper() != Looper.getMainLooper()) {
		//	runOnUiThread(new Runnable() {
		//		@Override
		//		public void run() {
		//			appendLog(st);
		//		}
		//	});
		//}
		tvLog.append(st);
		tvLog.append("\n");
		//滚动到底部
		tvLog.scrollTo(0, tvLog.getLineCount() * tvLog.getLineHeight() - (tvLog.getHeight() - (tvLog.getPaddingTop() * 2)));
	}

	private void clearLog() {
		//if (Looper.myLooper() != Looper.getMainLooper()) {
		//	runOnUiThread(new Runnable() {
		//		@Override
		//		public void run() {
		//			clearLog();
		//		}
		//	});
		//}
		tvLog.setText("");
	}


}
