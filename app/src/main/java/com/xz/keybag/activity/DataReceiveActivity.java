package com.xz.keybag.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.service.SocketService;
import com.xz.keybag.utils.NetWorkUtil;

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
				initSocket();
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
		socketService.initSocket();
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


	@OnClick(R.id.tv_back)
	public void onViewClick(View v) {
		switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		stopService(serverIntent);
	}

}
