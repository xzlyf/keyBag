package com.xz.keybag.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.base.utils.PreferencesUtilV2;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.PasswordInputDialog;
import com.xz.keybag.sql.DBManager;
import com.xz.keybag.utils.PermissionsUtils;
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
	@BindView(R.id.sw_pwd_close)
	Switch swLoginSwitch;
	@BindView(R.id.slogan_save)
	TextView tvSloganSave;
	@BindView(R.id.et_slogan)
	EditText etSlogan;
	@BindView(R.id.pass_view)
	LinearLayout passView;

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
		Glide.with(this).asGif().load(R.drawable.animaiton_unlock_dark).into(bannerView);
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
		swLoginSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//如果不是用户按下的就不往下执行
				if (!buttonView.isPressed()) {
					return;
				}
				if (isChecked) {

					PasswordInputDialog pwdInputDialog = new PasswordInputDialog(mContext);
					pwdInputDialog.setOnClickListener(new PasswordInputDialog.PassDialogListener() {
						@Override
						public void onClick(PasswordInputDialog dialog, String st) {
							int i = db.updateLogin(Local.mAdmin.getLoginPwd(), st);
							if (i>0){
								sToast("已开启登录验证");
								db.updateLoginSwitch(Local.mAdmin.getConfig().getId(), Local.CONFIG_LOGIN_OPEN);
								passView.setVisibility(View.VISIBLE);
								swLoginSwitch.setChecked(true);
							}else{
								swLoginSwitch.setChecked(false);
								sToast("设置失败，请退出重试！");
							}
							dialog.dismiss();

						}
					});
					pwdInputDialog.create();
					pwdInputDialog.setOnCancelLickListener("取消 ", new PasswordInputDialog.PassDialogListener() {
						@Override
						public void onClick(PasswordInputDialog dialog, String st) {
							dialog.dismiss();
							swLoginSwitch.setChecked(false);
						}
					});
					pwdInputDialog.setTitle("用户须知");
					pwdInputDialog.setContent("设置登录密码可以提升[钥匙包]安全保障。\n用户需要牢记登陆密码，忘记将无法进入[钥匙包]噢！\n\n输入新的登录密码继续：");
					pwdInputDialog.show();
				} else {
					PasswordInputDialog pwdInputDialog = new PasswordInputDialog(mContext);
					pwdInputDialog.setOnClickListener(new PasswordInputDialog.PassDialogListener() {
						@Override
						public void onClick(PasswordInputDialog dialog, String st) {
							//验证登录密码
							if (st.equals(Local.mAdmin.getLoginPwd())) {
								db.updateLoginSwitch(Local.mAdmin.getConfig().getId(), Local.CONFIG_LOGIN_SHUT);
								dialog.dismiss();
								swLoginSwitch.setChecked(false);
								passView.setVisibility(View.GONE);
							} else {
								dialog.cleanStatus();
								sToast("密码验证失败");
								swLoginSwitch.setChecked(true);
							}
						}
					});
					pwdInputDialog.create();
					pwdInputDialog.setOnCancelLickListener("取消 ", new PasswordInputDialog.PassDialogListener() {
						@Override
						public void onClick(PasswordInputDialog dialog, String st) {
							dialog.dismiss();
							swLoginSwitch.setChecked(true);
						}
					});
					pwdInputDialog.setTitle("谨慎操作");
					pwdInputDialog.setContent("取消登录密码后，[钥匙包]将不再提供有效防护。\n任何人都可轻易访问到[钥匙包]，请谨慎操作。\n\n输入登录密码完成操作！");
					pwdInputDialog.show();
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
		//密码登录开关
		if (TextUtils.equals(Local.mAdmin.getConfig().getLoginSwitch(), Local.CONFIG_LOGIN_OPEN)) {
			swLoginSwitch.setChecked(true);
			passView.setVisibility(View.VISIBLE);
		} else {
			passView.setVisibility(View.GONE);
		}
		//标语slogan
		String slogan = PreferencesUtilV2.getString(Local.SHARD_SLOGAN, Local.DEFAULT_SLOGAN);
		etSlogan.setText(slogan);
	}

	@OnClick({R.id.tv_back, R.id.tv_change, R.id.tv_share, R.id.tv_login, R.id.slogan_save
			, R.id.tv_delete})
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

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//就多一个参数this
		PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}


	/**
	 * 清空Common表
	 */
	private void deleteAll() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(mContext)
					.setTitle("警告")
					.setMessage("继续讲删除所有已保存的密码数据\n请考虑清除是否要继续")
					.setNegativeButton("取消，不要删除", null)
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
