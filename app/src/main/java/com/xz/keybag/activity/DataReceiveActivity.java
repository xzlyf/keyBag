package com.xz.keybag.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.service.SocketService;

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
		socketService.initSocket();
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
