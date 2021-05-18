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
import java.io.InputStream;
import java.net.InetAddress;
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
		if (mServer == null && mServerThread == null) {
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
		if (mServer != null) {
			try {
				mServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mServer = null;
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
				mServer = new ServerSocket(20023, 1);
				callBack.created(mServer.getLocalPort());
				mClient = mServer.accept();
				Logger.d("已接入:"+mClient.getInetAddress().getHostAddress());
				InetAddress clientAddress = mClient.getInetAddress();
				callBack.isConnected(clientAddress.getHostAddress(), clientAddress.getHostName());

				InputStream inputStream = mClient.getInputStream();
				//byte[] buff = new byte[1024];
				//String st;
				//while (inputStream.read(buff) != -1) {
				//	Logger.i("接收：" + new String(buff));
				//}

				Logger.d("结束线程"+inputStream.read());


			} catch (IOException e) {
				if (TextUtils.equals(e.getMessage(), "Socket closed")) {
					Logger.d("Socket Close");
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
