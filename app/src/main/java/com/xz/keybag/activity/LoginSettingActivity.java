package com.xz.keybag.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.base.utils.PreferencesUtilV2;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.DBManager;
import com.xz.keybag.utils.lock.DES;
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
	@BindView(R.id.sw_pwd_public)
	Switch swPwdPublic;
	@BindView(R.id.slogan_save)
	TextView tvSloganSave;
	@BindView(R.id.et_slogan)
	EditText etSlogan;

	private DBManager db;
	private AlertDialog dialog;

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
		swPwdPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					db.updatePwdPublic(Local.mAdmin.getConfig().getId(), Local.CONFIG_PUBLIC_PWD_OPEN);
				} else {
					db.updatePwdPublic(Local.mAdmin.getConfig().getId(), Local.CONFIG_PUBLIC_PWD_SHUT);
				}
			}
		});
		etSlogan.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				tvSloganSave.setVisibility(View.VISIBLE);
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
		//密码防忘记
		if (TextUtils.equals(Local.mAdmin.getConfig().getForgetPass(), Local.CONFIG_FORGET_OPEN)) {
			swForgetPass.setChecked(true);
		}
		//密码明文显示
		if (TextUtils.equals(Local.mAdmin.getConfig().getPublicPwd(), Local.CONFIG_PUBLIC_PWD_OPEN)) {
			swPwdPublic.setChecked(true);
		}
		//标语slogan
		String slogan = PreferencesUtilV2.getString(Local.SHARD_SLOGAN, Local.DEFAULT_SLOGAN);
		etSlogan.setText(slogan);
	}

	@OnClick({R.id.tv_back, R.id.tv_change, R.id.tv_share, R.id.tv_login, R.id.slogan_save, R.id.tv_delete})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_change:
				startActivity(new Intent(LoginSettingActivity.this, ModifyActivity.class)
						.putExtra(Local.INTENT_EXTRA_QR_CODE, getQrSecret()));
				break;
			case R.id.tv_share:
				startActivity(new Intent(LoginSettingActivity.this, QRCodeActivity.class)
						.putExtra(Local.INTENT_EXTRA_QR_CODE, getQrSecret()));
				break;
			case R.id.tv_login:
				startActivity(new Intent(LoginSettingActivity.this, LoginPassActivity.class));
				break;
			case R.id.slogan_save:
				saveSlogan();
				break;
			case R.id.tv_delete:
				deleteAll();
				break;
		}
	}

	/**
	 * 清空Common表
	 */
	private void deleteAll() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(mContext)
					.setTitle("警告")
					.setMessage("继续讲删除所有已保存的密码数据\n请考虑清除是否要继续")
					.setNegativeButton("取消", null)
					.setPositiveButton("是的，我已考虑清楚", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							db.deleteAllProject();
						}
					})
					.create();
		}
		dialog.show();
	}


	/**
	 * 获取密钥
	 * 经过加密的
	 * 二维码传输协议:keybag_secret@RSA密文
	 * 格式：RSA(头协议@DES(secret))
	 */
	private String getQrSecret() {
		String secret = Local.mAdmin.getDes();
		if (TextUtils.isEmpty(secret)) {
			sToast("密钥文件已被篡改");
			return "error_secret";
		}
		StringBuilder qrSt = new StringBuilder();
		try {
			qrSt.append(Local.PROTOCOL_QR_SECRET);
			qrSt.append(Local.PROTOCOL_SPLIT);
			qrSt.append(DES.encryptor(secret, Local.desKey));
			return RSA.publicEncrypt(qrSt.toString(), RSA.getPublicKey(Local.publicKey));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			sToast("密钥文件已被损坏");
			return "error_failure";
		}
	}

	private void saveSlogan() {
		String slogan = etSlogan.getText().toString();
		PreferencesUtilV2.putString(Local.SHARD_SLOGAN, slogan);
		Local.SLOGAN = slogan;
		sToast("已保存");
	}
}
