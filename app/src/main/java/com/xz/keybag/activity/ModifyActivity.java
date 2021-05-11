package com.xz.keybag.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyActivity extends BaseActivity {

	@BindView(R.id.btn_camera)
	Button btnCamera;
	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.banner)
	ImageView banner;

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
		changeStatusBarTextColor();
		Glide.with(this).asGif().load(R.drawable.animaiton_data).into(banner);
	}

	@OnClick({R.id.btn_camera, R.id.tv_back})
	public void onViewClick(View v) {
		switch (v.getId()) {
			case R.id.btn_camera:
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
			String scanResult = bundle.getString(Local.INTENT_EXTRA_KEY_QR_SCAN);
			//将扫描出的信息显示出来
			Logger.w("二维码内容:" + scanResult);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO: add setContentView(...) invocation
		ButterKnife.bind(this);
	}
}
