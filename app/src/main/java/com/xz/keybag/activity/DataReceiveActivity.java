package com.xz.keybag.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.service.SocketService;
import com.xz.keybag.utils.NetWorkUtil;
import com.xz.keybag.utils.PermissionsUtils;
import com.xz.keybag.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DataReceiveActivity extends BaseActivity {

	@BindView(R.id.tv_log)
	TextView tvLog;

	private ServiceConnection sc;
	public SocketService socketService;
	private Intent serverIntent;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_data_receive;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		bindSocketService();
	}


	private void bindSocketService() {
		/*通过binder拿到service*/
		sc = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
				SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
				socketService = binder.getService();
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {

			}
		};
		serverIntent = new Intent(getApplicationContext(), SocketService.class);
		bindService(serverIntent, sc, BIND_AUTO_CREATE);
	}

	private void initSocket() {
		//获取当前ip
		String host = checkConnectType();
		if (host == null) {
			return;
		}
		socketService.initSocket("192.168.1.66", 20022);
	}

	/**
	 * 检查网络状态是否符合
	 *
	 * @return 如果符合返回当前Ip
	 */
	public String checkConnectType() {
		int connectedType = NetWorkUtil.getConnectedType(mContext);
		if (connectedType == 1) {
			//网络状态符合 wifi
			return NetWorkUtil.getIpInWifi(mContext);
		}
		//网络状态不符合
		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setMessage("请先开启WIFI\n确保与接收方在同一个WIFi下")
				.setPositiveButton("开启wifi", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//跳转到配置wifi界面
						startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				})
				.create();
		dialog.show();

		return null;
	}


	@OnClick({R.id.tv_back, R.id.image_scan})
	public void onViewClick(View v) {
		switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.image_scan:
				startQrCode();
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
						Intent intent = new Intent(DataReceiveActivity.this, CaptureActivity.class);
						startActivityForResult(intent, Local.REQ_QR_CODE);
					}

					@Override
					public void forbitPermissons() {
						AlertDialog finallyDialog = new AlertDialog.Builder(DataReceiveActivity.this)
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

	private void handleQrCode(String scanResult) {
		Logger.d("报文："+scanResult);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		stopService(serverIntent);
	}


}
