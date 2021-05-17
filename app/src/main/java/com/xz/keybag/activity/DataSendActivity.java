package com.xz.keybag.activity;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataSendActivity extends BaseActivity {


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

	}



	/**
	 * 服务端部署
	 */
	private class ServerSocketDeploy extends Thread {
		@Override
		public void run() {
			ServerSocket server = null;
			try {
				//backlog 连接队列最大长度  1
				server = new ServerSocket(20022, 1);
				Socket client = server.accept();

			} catch (IOException e) {
				Logger.e("ServerSocketDeploy Error " + e.getMessage());
			}


		}
	}
}
