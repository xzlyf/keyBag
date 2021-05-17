package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketService extends Service {

	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private ServerSocket mServer = null;
	private ServerDeployThread mServerThread = null;
	private Socket mClient;

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
		if (mServer == null && mServerThread == null) {
			mServerThread = new ServerDeployThread();
			mServerThread.start();
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
				mServer = new ServerSocket(20022, 1);
				callBack.created(mServer.getLocalPort());
				mClient = mServer.accept();

			} catch (IOException e) {
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
		public void created(int port) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.created(port);
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

		/**
		 * 抛异常了
		 */
		void error(Exception e);
	}
}
