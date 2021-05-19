package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.orhanobut.logger.Logger;

public class SocketService extends Service {
	private final int TIME_OUT_CONNECT = 5 * 1000;
	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());


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
	}

	/**
	 * 释放连接
	 */
	public void releaseSocket() {
		Logger.w("释放连接");
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
