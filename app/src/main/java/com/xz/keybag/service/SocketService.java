package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketService extends Service {
	private final int TIME_OUT_CONNECT = 5 * 60 * 1000;
	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private ServerDeployThread mServerThread = null;
	private Socket mClient;

	public SocketService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return socketBinder;
	}

	public class SocketBinder extends Binder {
		public SocketService getService() {
			return SocketService.this;
		}
	}


	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//释放socket
		releaseSocket();
	}

	/**
	 * Service通过回调返回数据给Activity
	 */
	public void setCallback(SocketCallBack callback) {
		this.mCallback = callback;
	}

	/**
	 * 初始化ServerSocket
	 */
	public void initSocket() {
		if (mClient == null && mServerThread == null) {
			mServerThread = new ServerDeployThread();
			mServerThread.start();
		}
	}

	/**
	 * 释放连接
	 */
	public void releaseSocket() {
		if (mClient != null) {
			try {
				mClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mClient = null;
		}
		if (mServerThread != null && mServerThread.isAlive()) {
			mServerThread.interrupt();
			mServerThread = null;
		}
	}

	/**
	 * 服务端部署
	 */
	private class ServerDeployThread extends Thread {
		@Override
		public void run() {

			try {
				//backlog 连接队列最大长度  1
				mClient = new Socket();
				mClient.connect(new InetSocketAddress("192.168.1.66", 20022), TIME_OUT_CONNECT);
				if (mClient.isConnected()) {
					Logger.e("连接成功");
				} else {
					Logger.e("连接失败");
				}
			} catch (IOException e) {
				if (TextUtils.equals(e.getMessage(), "Socket closed")) {
					Logger.i("Socket Close");
					return;
				}
				mCallback.error(e);
				Logger.e("ServerSocketDeploy Error " + e.getMessage());
			}


		}
	}


	/**
	 * 回调做回到主线处理
	 */
	private SocketCallBack callBack = new SocketCallBack() {

		@Override
		public void error(Exception e) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.error(e);
				}
			});
		}
	};

	public interface SocketCallBack {

		/**
		 * 抛异常了
		 */
		void error(Exception e);
	}
}
