package com.xz.keybag.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.keybag.utils.lock.RSA;
import com.xz.keybag.zxing.activity.CaptureActivity;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
	@BindView(R.id.change_secret)
	LinearLayout changeSecret;
	@BindView(R.id.tv_close)
	TextView tvClose;

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
		changeStatusBarTextColor();
		String qrSt = getIntent().getStringExtra("qr_code");
		if (TextUtils.isEmpty(qrSt)) {
			finish();
			return;
		}
		db = DBManager.getInstance(this);
		currentSecret.setText(verifySecret(qrSt));
	}

	@OnClick({R.id.tv_back, R.id.open_camera, R.id.tv_close, R.id.change_secret})
	public void onViewClick(View v) {
		switch (v.getId()) {
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
			case R.id.change_secret:
				startChange();
				break;
		}
	}


	/**
	 * 调用zxing扫描二维码
	 */
	private void startQrCode() {
		// 申请相机权限
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			// 申请权限
			ActivityCompat.requestPermissions(ModifyActivity.this, new String[]{Manifest.permission.CAMERA}, Local.REQ_PERM_CAMERA);
			return;
		}
		// 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			// 申请权限
			ActivityCompat.requestPermissions(ModifyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Local.REQ_PERM_EXTERNAL_STORAGE);
			return;
		}
		// 二维码扫码
		Intent intent = new Intent(ModifyActivity.this, CaptureActivity.class);
		startActivityForResult(intent, Local.REQ_QR_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case Local.REQ_PERM_CAMERA:
				// 摄像头权限申请
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获得授权
					startQrCode();
				} else {
					// 被禁止授权
					Toast.makeText(ModifyActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
				}
				break;
			case Local.REQ_PERM_EXTERNAL_STORAGE:
				// 文件读写权限申请
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获得授权
					startQrCode();
				} else {
					// 被禁止授权
					Toast.makeText(ModifyActivity.this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
				}
				break;
		}
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
		String secret = verifySecret(qrSt);
		if (secret.equals("no_message") || secret.equals("error_code")) {
			return;
		}

		newSecretLayout.setVisibility(View.VISIBLE);
		newSecret.setText(secret);
		changeSecret.setEnabled(true);
		changeSecret.setVisibility(View.VISIBLE);
		try {
			xtSecret = RSA.privateDecrypt(secret, RSA.getPrivateKey(Local.privateKey));
		} catch (Exception e) {
			e.printStackTrace();
			xtSecret = null;
		}

	}

	/**
	 * 验证密钥文本
	 */
	private String verifySecret(String secret) {
		//二维码报文头判断 keybag_secret@RSA密文
		String[] qrArray = secret.split("@");
		if (qrArray.length != 2) {
			sToast("这个二维码没有我想要的信息(ˉ▽ˉ；)...");
			return "no_message";
		}

		if (!qrArray[0].equals("keybag_secret")) {
			sToast("非法二维码");
			return "error_code";
		}

		return qrArray[1];
	}


	/**
	 * 开始修改密钥
	 */
	private void startChange() {
		changeSecret.setEnabled(false);
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
		boolean isOK = db.testSecret(xtSecret);
		if (!isOK) {
			appendLog("密钥可能不兼容");
		}
		appendLog("正在替换旧密钥...");
		int state = db.updateSecret(Local.mAdmin.getDes(), xtSecret);
		appendLog("更新状态：" + state);
		Local.mAdmin.setDes(xtSecret);
		appendLog("修改密钥成功");
		appendLog("----结束修改密钥----");

		changeSecret.setVisibility(View.GONE);
		openCamera.setEnabled(true);
		tvClose.setEnabled(true);

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


	/**
	 * 获取密钥
	 * 经过加密的
	 * 二维码传输协议:keybag_secret@RSA密文
	 */
	private String getQrSecret() {
		String secret = Local.mAdmin.getDes();
		if (TextUtils.isEmpty(secret)) {
			sToast("密钥文件已被篡改");
			return "error_secret";
		}
		StringBuilder qrSt = new StringBuilder();
		try {
			qrSt.append("keybag_secret");
			qrSt.append("@");
			qrSt.append(RSA.publicEncrypt(secret, RSA.getPublicKey(Local.publicKey)));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			sToast("密钥文件已被损坏");
			return "error_failure";
		}
		//Logger.w("加密：" + Local.mAdmin.getDes());
		//Logger.w("生成：" + qrSt.toString());
		//Logger.w("解密：" + RSA.privateDecrypt(qrSt.toString(),RSA.getPrivateKey(Local.privateKey)));
		return qrSt.toString();
	}
}
