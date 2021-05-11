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
import com.xz.keybag.utils.lock.RSA;
import com.xz.keybag.zxing.activity.CaptureActivity;

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

	private boolean isSafeSecret = false;//是否合法密钥
	private String xtSecret;//des明文密钥

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
		currentSecret.setText(verifySecret(qrSt));
	}

	@OnClick({R.id.tv_back, R.id.open_camera})
	public void onViewClick(View v) {
		switch (v.getId()) {
			case R.id.open_camera:
				startQrCode();
				break;
			case R.id.tv_back:
				finish();
				break;
		}
	}

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
		newSecretLayout.setVisibility(View.VISIBLE);
		newSecret.setText(verifySecret(qrSt));

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

		try {
			xtSecret = RSA.privateDecrypt(secret, RSA.getPrivateKey(Local.privateKey));
		} catch (Exception e) {
			e.printStackTrace();
			xtSecret = null;
		}
		return qrArray[1];
	}

}
