package com.xz.keybag.activity;

import android.Manifest;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.NumberKeyboard;
import com.xz.keybag.custom.PasswordInputDialog;
import com.xz.keybag.entity.AdminConfig;
import com.xz.keybag.fingerprint.FingerprintHelper;
import com.xz.keybag.fingerprint.OnAuthResultListener;
import com.xz.keybag.jni.NativeUtils;
import com.xz.keybag.sql.DBHelper;
import com.xz.keybag.sql.DBManager;
import com.xz.keybag.utils.DeviceUniqueUtils;
import com.xz.keybag.utils.IOUtil;
import com.xz.keybag.utils.PermissionsUtils;
import com.xz.keybag.utils.UUIDUtil;
import com.xz.utils.hardware.SystemInfoUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {

	@BindView(R.id.input_type)
	TextView inputType;
	@BindView(R.id.number_view)
	NumberKeyboard numberView;
	@BindView(R.id.et_pwd)
	TextView etPwd;
	//layout1 密码输入
	@BindView(R.id.input_layout)
	LinearLayout inputLayout;
	//layout2 指纹登录
	@BindView(R.id.input_layout_2)
	LinearLayout inputLayout2;
	@BindView(R.id.tv_tips)
	TextView tvTips;
	@BindView(R.id.tv_input_tips)
	TextView tvInputTips;

	private FingerprintHelper fingerprintHelper;
	//震动幅度
	private float shakeDegrees = 3f;
	private Vibrator vibrator;
	private int mode;
	private DBManager db;
	private String deviceId;
	private boolean isSaveUnlockTime = false;
	private long newLoginTime;
	private String configId;
	private final long[] VIBRATE_ERROR = new long[]{0, 80, 80, 80};
	private final int VIBRATE_CLICK = 50;
	private boolean noFingerprint = false;

	@Override
	public boolean homeAsUpEnabled() {
		return false;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login;
	}

	@Override
	public void initData() {
		//Logger.w("签名：" + AppInfoUtils.getPackageSign(this, false));
		//Logger.w("sign加密：" + NativeUtils.signatureParams("utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation"));

		if (getIntent() != null) {
			mode = getIntent().getIntExtra("mode", 0);
		}
		initView();
		//震动服务
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		if (mode == Local.START_MODE_LOGIN_MODE) {
			//initSql
			DBHelper.DB_PWD = NativeUtils.signatureParams("KeyBag_Secret");//生成数据库密码
			db = DBManager.getInstance(this);
			//管理唯一标识
			initIdentity();
			//登录初始化
			initLogin();
			//查询登录配置
			initLoginConfig();
		} else if (mode == Local.START_MODE_SECRET_SETTING) {
			//用户是否开启指纹登录
			if (!Local.mAdmin.getFingerprint().equals(Local.FINGERPRINT_STATE_OPEN)) {
				inputLayout2.setVisibility(View.GONE);
				inputLayout.setVisibility(View.VISIBLE);
				inputType.setVisibility(View.GONE);
				noFingerprint = true;
			} else {
				//初始化指纹模块
				initFingerprint();
			}
		} else {
			finish();
		}

		//关闭密码验证
		if (Local.mAdmin != null) {
			if (!TextUtils.equals(Local.mAdmin.getConfig().getLoginSwitch(), Local.CONFIG_LOGIN_OPEN)) {
				killMySelf();
			}
		}


	}

	private void initView() {
		inputLayout.setVisibility(View.GONE);
		inputType.setVisibility(View.VISIBLE);
		inputLayout2.setVisibility(View.VISIBLE);
		numberView.setOnCallBack(new NumberKeyboard.CallBack() {
			@Override
			public void clickNum(String num) {
				playVibration();
				if (etPwd.getText().toString().trim().length() == Local.PWD_COUNT - 1) {
					etPwd.append(num);
					checkPwd();
					return;
				} else if (etPwd.getText().toString().trim().length() >= Local.PWD_COUNT) {
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


	/**
	 * 管理设备唯一id
	 */
	private void initIdentity() {
		//检查权限，获取设备唯一id
		PermissionsUtils.getInstance().chekPermissions(this,
				new String[]{Manifest.permission.READ_PHONE_STATE},
				new PermissionsUtils.IPermissionsResult() {
					@Override
					public void passPermissons() {
						deviceId = getDeviceUnique();
						String old = db.queryIdentity();
						if (old != null) {
							if (!old.equals(deviceId)) {
								//跟之前保存的唯一标识不一致，环境异常，提示是否确认风险，确认同意后后就存入此次新的唯一标识
								AlertDialog riskDialog = new AlertDialog.Builder(LoginActivity.this)
										.setTitle("风险提示")
										.setMessage("检测到设备异常\n是否继续")
										.setNegativeButton("无风险，继续", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
												db.insertIdentity(deviceId);
											}
										})
										.setPositiveButton("退出", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
												finish();
											}
										})
										.setCancelable(false)
										.create();
								riskDialog.show();
							}
						} else {
							//存入唯一标识
							db.insertIdentity(deviceId);
						}
					}

					@Override
					public void forbitPermissons() {
						AlertDialog finallyDialog = new AlertDialog.Builder(LoginActivity.this)
								.setMessage("App需要通过此权限获取设备id，\n以确保数据安全性。\n否则无法进行下一步")
								.setCancelable(false)
								.setPositiveButton("退出", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										finish();
									}
								})
								.create();
						finallyDialog.show();

					}
				});
	}

	/**
	 * 初始化登录相关操作
	 */
	private void initLogin() {
		//获取密码状态
		String loginState = db.login();
		//没有密码 第一次进入
		if (loginState.equals(Local.PASSWORD_STATE_NULL)) {
			PasswordInputDialog pwdInputDialog = new PasswordInputDialog(this);
			pwdInputDialog.setOnClickListener(new PasswordInputDialog.PassDialogListener() {
				@Override
				public void onClick(PasswordInputDialog dialog, String st) {
					dialog.dismiss();
					try {
						db.initSecret(st);
						//初始化指纹模块
						initFingerprint();
					} catch (Exception e) {
						sToast(e.getMessage());
						finish();
					}
				}
			});
			pwdInputDialog.create();
			pwdInputDialog.setTitle("设置密码");
			pwdInputDialog.setContent("App需要设置一个登录密码，\n以确保数据安全性。\n请用户务必记住密码，\nApp不提供找回密码的服务");
			pwdInputDialog.show();
		} else if (loginState.equals(Local.PASSWORD_STATE_SUCCESS)) {
			//用户是否开启指纹登录
			if (!Local.mAdmin.getFingerprint().equals(Local.FINGERPRINT_STATE_OPEN)) {
				inputLayout2.setVisibility(View.GONE);
				inputLayout.setVisibility(View.VISIBLE);
				inputType.setVisibility(View.GONE);
			} else {
				//初始化指纹模块
				initFingerprint();
			}

		} else {
			AlertDialog dialog = new AlertDialog.Builder(mContext)
					.setMessage("密码文件被篡改，数据丢失")
					.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.create();
			dialog.show();
		}
	}


	/**
	 * 初始化指纹 ，如果有
	 */
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
				inputType.setVisibility(View.GONE);
				Local.mAdmin.setFingerprint(Local.FINGERPRINT_STATE_NONSUPPORT);
				noFingerprint = true;

			}

		});
		//开始监听
		fingerprintHelper.startListening();
	}


	/**
	 * 杀死自己-_-
	 */
	private void killMySelf() {
		//判断模式，打开对应的活动
		if (mode == Local.START_MODE_SECRET_SETTING) {
			startActivity(new Intent(mContext, LoginSettingActivity.class));
			finish();
			return;
		}
		updateLoginConfig();
		startActivity(new Intent(mContext, HomeActivity.class));
		overridePendingTransition(R.anim.translation_finish, R.anim.translation_create);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//一秒后销毁活动
				finish();
			}
		}, 500);
	}

	/**
	 * 启用用户的登录配置
	 */
	private void initLoginConfig() {
		AdminConfig config = db.queryAdminConfig();
		newLoginTime = System.currentTimeMillis();
		configId = config.getId();
		//获取用户是否开启密码防忘记功能
		if (TextUtils.equals(config.getForgetPass(), Local.CONFIG_FORGET_OPEN)) {
			//判断上次解锁日期是否超过3天 或者lastUnlockTime==1000
			if (newLoginTime - config.getUnlockTimestamp() >= 259200000L || config.getUnlockTimestamp() == 1000) {
				//优先显示密码输入
				inputLayout.setVisibility(View.VISIBLE);
				tvInputTips.setText("本次进入推荐使用密码");
			}
		}

	}

	/**
	 * 更新用户配置
	 */
	private void updateLoginConfig() {
		if (isSaveUnlockTime) {
			db.updateLoginTime(configId, newLoginTime, newLoginTime);
		} else {
			db.updateLoginTime(configId, newLoginTime, -1);
		}
	}


	private void checkPwd() {
		String temp = etPwd.getText().toString().trim();
		if (temp.equals("")) {
			return;
		}

		//temp = MD5Util.getMD5(temp);
		if (temp.equals(Local.mAdmin.getLoginPwd())) {
			isSaveUnlockTime = true;
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
		objectAnimator.setDuration(1000);
		objectAnimator.start();
	}

	/**
	 * 数字点击震动效果
	 */
	private void playVibration() {
		vibrator.vibrate(VIBRATE_CLICK);
	}

	/**
	 * 错误震动
	 */
	private void playErrorVibration() {
		vibrator.vibrate(VIBRATE_ERROR, -1);
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
	 * 获取设备唯一标识
	 * android 29开始 不再能获取imei
	 * 现在思路是低于29的照常获取imei
	 * 大于29的通过在外置私有目录创建一个文件保存uuid作为唯一标识
	 * 但是用户卸载后标识会被删除
	 *
	 * @return
	 */
	private String getDeviceUnique() {
		String devices = "";
		int version = SystemInfoUtil.getSystemVersionCode();
		if (version >= Build.VERSION_CODES.Q) {
			File extPath = getExternalFilesDir("IMEI");
			if (extPath == null) {
				return "not found sd card";
			}
			if (!extPath.exists()) {
				boolean mkdir = extPath.mkdir();
				if (!mkdir) {
					return "-1";
				}
			}
			extPath = new File(extPath, "imei");

			if (extPath.exists()) {
				devices = read(extPath);
			} else {
				devices = write(extPath);
			}

		} else {
			devices = DeviceUniqueUtils.getPhoneSign(LoginActivity.this);
		}

		Logger.d("imei:" + devices);
		return devices;
	}

	/**
	 * 读取imei文件
	 */
	private String read(File extPath) {
		String imei = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(extPath));
			imei = reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			imei = "imei read error";
		} finally {
			IOUtil.closeAll(reader);
		}
		return imei;
	}

	/**
	 * 写入imei文件
	 */
	private String write(File extPath) {
		String imei = UUIDUtil.getStrUUID();
		FileWriter writer = null;
		try {
			writer = new FileWriter(extPath);
			writer.write(imei);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			imei = "imei write error";
		} finally {
			IOUtil.closeAll(writer);
		}

		return imei;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//就多一个参数this
		PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

	@Override
	public void onBackPressed() {
		if (inputLayout.getVisibility() == View.VISIBLE) {
			if (noFingerprint) {
				super.onBackPressed();
			} else {
				inputLayout.setVisibility(View.GONE);
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (fingerprintHelper != null) {
			fingerprintHelper.stopListener();
		}
	}


}
