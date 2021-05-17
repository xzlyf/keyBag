package com.xz.keybag.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.service.ServerSocketService;

public class DataSendActivity extends BaseActivity {

	private ServiceConnection sc;
	public ServerSocketService socketService;
	private Intent serverIntent;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_data_send;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		bindSocketService();
		//initServerSocket();
	}

	private void bindSocketService() {
		/*通过binder拿到service*/
		sc = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
				ServerSocketService.SocketBinder binder = (ServerSocketService.SocketBinder) iBinder;
				socketService = binder.getService();

			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {

			}
		};
		serverIntent = new Intent(getApplicationContext(), ServerSocketService.class);
		bindService(serverIntent, sc, BIND_AUTO_CREATE);
	}


	private void initServerSocket() {
		socketService.setCallback(new ServerSocketService.SocketCallBack() {
			@Override
			public void created(int port) {

			}

			@Override
			public void error(Exception e) {

			}
		});
		socketService.initSocket();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		stopService(serverIntent);
	}

}
