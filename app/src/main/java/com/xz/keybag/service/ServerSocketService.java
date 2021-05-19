package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class ServerSocketService extends Service {

	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());


	public ServerSocketService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return socketBinder;
	}

	public class SocketBinder extends Binder {

		/*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
		public ServerSocketService getService() {
			return ServerSocketService.this;
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
	}

	/**
	 * 释放连接
	 */
	public void releaseSocket() {
	}


	/**
	 * 回调做回到主线处理
	 */
	private SocketCallBack callBack = new SocketCallBack() {
		@Override
		public void created(int port) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.created(port);
				}
			});
		}

		@Override
		public void isConnected(String ip, String name) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.isConnected(ip, name);
				}
			});
		}

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
		 * socket创建完成
		 */
		void created(int port);

		void isConnected(String ip, String name);

		/**
		 * 抛异常了
		 */
		void error(Exception e);
	}
}
