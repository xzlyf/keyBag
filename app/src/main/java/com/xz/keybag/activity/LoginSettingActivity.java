package com.xz.keybag.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.keybag.utils.lock.RSA;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginSettingActivity extends BaseActivity {


	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.banner)
	ImageView bannerView;
	@BindView(R.id.tv_change)
	TextView tvChange;
	@BindView(R.id.tv_share)
	TextView tvShare;
	@BindView(R.id.sw_fingerprint)
	Switch swFingerprint;
	@BindView(R.id.sw_forget)
	Switch swForgetPass;
	private DBManager db;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login_setting;
	}

	@Override
	public void initData() {
		if (Local.mAdmin == null) {
			sToast("请先初始化");
			return;
		}
		db = DBManager.getInstance(this);
		changeStatusBarTextColor();
		Glide.with(this).asGif().load(R.drawable.animaiton_unlock).into(bannerView);
		initState();
		swFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					db.updateFingerprintLogin(Local.FINGERPRINT_STATE_OPEN);
					sToast("已开启指纹登录");
				} else {
					db.updateFingerprintLogin(Local.FINGERPRINT_STATE_CLOSE);
					sToast("已关闭指纹登录");
				}
			}
		});
		swForgetPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					db.updateForgetPassState(Local.mAdmin.getConfig().getId(), Local.CONFIG_FORGET_OPEN);
				} else {
					db.updateForgetPassState(Local.mAdmin.getConfig().getId(), Local.CONFIG_FORGET_SHUT);
				}
			}
		});
	}

	/**
	 * 刷新状态
	 */
	private void initState() {
		//指纹登录状态
		if (TextUtils.equals(Local.mAdmin.getFingerprint(), Local.FINGERPRINT_STATE_OPEN)) {
			swFingerprint.setChecked(true);
		} else if (TextUtils.equals(Local.mAdmin.getFingerprint(), Local.FINGERPRINT_STATE_NONSUPPORT)) {
			swFingerprint.setChecked(false);
			swFingerprint.setEnabled(false);
			swFingerprint.setVisibility(View.GONE);
		} else {
			swFingerprint.setChecked(false);
		}
		if (TextUtils.equals(Local.mAdmin.getConfig().getForgetPass(), Local.CONFIG_FORGET_OPEN)) {
			swForgetPass.setChecked(true);
		}
	}

	@OnClick({R.id.tv_back, R.id.tv_change, R.id.tv_share, R.id.tv_login})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_change:
				startActivity(new Intent(LoginSettingActivity.this, ModifyActivity.class)
						.putExtra("qr_code", getQrSecret()));
				break;
			case R.id.tv_share:
				startActivity(new Intent(LoginSettingActivity.this, QRCodeActivity.class)
						.putExtra("qr_code", getQrSecret()));
				break;
			case R.id.tv_login:
				startActivity(new Intent(LoginSettingActivity.this, LoginPassActivity.class));
				break;
		}
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
