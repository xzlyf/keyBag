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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.service.SocketService;
import com.xz.keybag.utils.NetWorkUtil;
import com.xz.keybag.utils.PermissionsUtils;
import com.xz.keybag.utils.lock.RSA;
import com.xz.keybag.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DataReceiveActivity extends BaseActivity {

	@BindView(R.id.tv_log)
	TextView tvLog;
	@BindView(R.id.image_scan)
	ImageView imageScan;

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
		bindSocketService();
	}


	private void bindSocketService() {
		/*通过binder拿到service*/
		sc = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
				SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
				socketService = binder.getService();
				socketService.setCallback(new SocketService.SocketCallBack() {
					@Override
					public void isConnected() {
						imageScan.setVisibility(View.GONE);
						disLoading();
					}

					@Override
					public void connectFailed() {
						disLoading();
					}

					@Override
					public void verifyFailed() {
						sToast("发送端app验证异常，不可完成传输");
					}

					@Override
					public void message(String msg) {
						appendLog(msg);
					}

					@Override
					public void finish() {

					}

					@Override
					public void error(Exception e) {
						appendLog(e.getMessage());

					}
				});
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {

			}
		};
		serverIntent = new Intent(getApplicationContext(), SocketService.class);
		bindService(serverIntent, sc, BIND_AUTO_CREATE);
	}

	private void initSocket(String ip, int port) {
		showLoading(" 正在连接发送端", false, null);
		socketService.initSocket(ip, port);
	}


	/**
	 * 检查网络状态是否符合
	 *
	 * @return 如果符合返回当前Ip
	 */
	private String checkConnectType() {
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
				//获取当前内网ip
				String host = checkConnectType();
				if (host == null) {
					return;
				}
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
		//解密RSA
		try {
			scanResult = RSA.privateDecrypt(scanResult, RSA.getPrivateKey(Local.privateKey));
		} catch (Exception e) {
			sToast("这个二维码没有我想要的信息(ˉ▽ˉ；)...");
			return;
		}
		//头协议判断
		String[] qrArray = scanResult.split(Local.PROTOCOL_SPLIT);
		if (qrArray.length != 3) {
			sToast("不支持此协议");
			return;
		}
		if (!qrArray[0].equals(Local.PROTOCOL_QR_SHARD)) {
			sToast("非法二维码");
			return;
		}

		//开启socket服务
		initSocket(qrArray[1], Integer.parseInt(qrArray[2]));

	}


	/**
	 * 输出log
	 * 自动滚动到底部
	 */
	private void appendLog(String st) {
		tvLog.append(st);
		tvLog.append("\n");
		//滚动到底部
		tvLog.scrollTo(0, tvLog.getLineCount() * tvLog.getLineHeight() - (tvLog.getHeight() - (tvLog.getPaddingTop() * 2)));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		stopService(serverIntent);
	}


}
