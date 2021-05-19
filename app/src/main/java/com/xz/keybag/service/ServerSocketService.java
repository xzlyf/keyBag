package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
		new Thread(new Runnable() {
			@Override
			public void run() {
				start();
				// TODO: 2021/5/20 新问题，socket可以连接上，但是得不到输入输出流
			}
		}).start();
	}

	int port = 20022;

	void start() {
		Socket s = null;
		try {
			ServerSocket ss = new ServerSocket(port);
			while (true) {
				// 选择进行传输的文件
				String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/five.rar";
				//String filePath = "D:\\lib.rar";
				File fi = new File(filePath);

				System.out.println("文件长度:" + (int) fi.length());

				// public Socket accept() throws
				// IOException侦听并接受到此套接字的连接。此方法在进行连接之前一直阻塞。

				s = ss.accept();
				System.out.println("建立socket链接");
				DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
				dis.readByte();

				DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
				DataOutputStream ps = new DataOutputStream(s.getOutputStream());
				//将文件名及长度传给客户端。这里要真正适用所有平台，例如中文名的处理，还需要加工，具体可以参见Think In Java 4th里有现成的代码。
				ps.writeUTF(fi.getName());
				ps.flush();
				ps.writeLong((long) fi.length());
				ps.flush();

				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];

				while (true) {
					int read = 0;
					if (fis != null) {
						read = fis.read(buf);
					}

					if (read == -1) {
						break;
					}
					ps.write(buf, 0, read);
				}
				ps.flush();
				// 注意关闭socket链接哦，不然客户端会等待server的数据过来，
				// 直到socket超时，导致数据不完整。
				fis.close();
				s.close();
				System.out.println("文件传输完成");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
