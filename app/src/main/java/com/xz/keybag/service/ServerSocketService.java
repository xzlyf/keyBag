package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.google.gson.Gson;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.keybag.utils.StorageUtil;
import com.xz.utils.MD5Util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerSocketService extends Service {

	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private ServerSocket ss;
	private RunningThread runningThread = null;
	private DBManager db;
	private String cachePath;

	public ServerSocketService() {
		db = DBManager.getInstance(this);
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
		if (runningThread == null) {
			runningThread = new RunningThread();
			runningThread.start();
		}
	}

	/**
	 * 释放连接
	 */
	public void releaseSocket() {
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (runningThread != null) {
			runningThread.interrupt();
		}
	}


	private class RunningThread extends Thread {

		@Override
		public void run() {
			createCache();
			deploySocket();
		}

		/**
		 * 生成传输文件
		 */
		private void createCache() {
			List<Project> projects = db.queryProject();
			File cacheFile = new File(StorageUtil.getCacheDir(ServerSocketService.this), String.valueOf(System.currentTimeMillis()));
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(cacheFile);
				Gson gson = new Gson();
				byte[] buff;
				for (Project p : projects) {
					buff = gson.toJson(p).getBytes();
					fos.write(buff);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			String rename = MD5Util.getFileMD5(cacheFile);
			File newCache = new File(StorageUtil.getCacheDir(ServerSocketService.this), rename);
			cacheFile.renameTo(newCache);
			cachePath = newCache.getAbsolutePath();
		}

		private void deploySocket() {
			int port = 20022;

			Socket s = null;
			try {
				ss = new ServerSocket(port);
				while (true) {

					s = ss.accept();
					System.out.println("建立socket链接");
					DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
					String verify = dis.readUTF();
					//校验信息
					//String[] split = verify.split("@");
					//if (split.length == 1) {
					//	System.out.println("验证不通过，关闭连接");
					//	dis.close();
					//	s.close();
					//	continue;
					//}


					// 选择进行传输的文件
					File fi = new File(cachePath);

					System.out.println("文件长度:" + (int) fi.length());

					DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(cachePath)));
					DataOutputStream ps = new DataOutputStream(s.getOutputStream());
					//将文件名及长度传给客户端。这里要真正适用所有平台，例如中文名的处理，还需要加工，具体可以参见Think In Java 4th里有现成的代码。
					ps.writeUTF(fi.getName());
					ps.flush();
					ps.writeLong(fi.length());
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
