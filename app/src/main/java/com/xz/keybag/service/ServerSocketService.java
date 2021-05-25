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
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class ServerSocketService extends Service {

	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private ServerSocket ss;
	private RunningThread runningThread = null;
	private DBManager db;
	private String cachePath;
	private int port = 20022;//主端口
	private int bPort = 20022;//备用端口

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
		int reTryNum = 0;

		@Override
		public void run() {
			createCache();
			deploySocket(port);
			clearCache();
		}

		/**
		 * 请里缓存文件
		 */
		private void clearCache() {
			File file = new File(cachePath);
			if (file.exists()) {
				file.delete();
				System.out.println("缓存文件已清除");
			}
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

		/**
		 * 开始部署
		 */
		private void deploySocket(int port) {

			//创建服务端
			try {
				ss = new ServerSocket(port);
			} catch (IOException e) {
				if (e instanceof BindException) {
					if (reTryNum >= 3) {
						System.out.println("超过重试次数，结束部署");
						return;
					}
					//随机生成40000至50000范围的端口
					int max = 50000;
					int min = 40000;
					Random random = new Random();
					int sPort = random.nextInt(max) % (max - min + 1) + min;
					reTryNum++;
					deploySocket(sPort);
					return;
				}
				System.out.println("服务器部署异常：" + e.getMessage());
				return;
			}
			System.out.println("服务端已部署,开始等待");
			callBack.created(ss.getLocalPort());


			//等待客户端接入
			Socket s = null;
			DataInputStream dis = null;
			try {
				while (true) {
					s = ss.accept();
					System.out.println("建立socket链接");
					dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
					String verify = dis.readUTF();
					//校验信息
					String[] split = verify.split("@");
					if (split.length == 1) {
						System.out.println("验证不通过，关闭连接");
						dis.close();
						s.close();
					} else {
						InetAddress inetAddress = s.getInetAddress();
						callBack.isConnected(inetAddress.getHostAddress(), inetAddress.getHostName());
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				callBack.close();
			}

			// 选择进行传输的文件
			try {
				File fi = new File(cachePath);
				System.out.println("文件长度:" + (int) fi.length());
				DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(cachePath)));
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				//将文件名及长度传给客户端。这里要真正适用所有平台，例如中文名的处理，还需要加工，具体可以参见Think In Java 4th里有现成的代码。
				dos.writeUTF(fi.getName());
				dos.flush();
				dos.writeLong(fi.length());
				dos.flush();

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
					dos.write(buf, 0, read);
				}
				dos.flush();
				// 注意关闭socket链接哦，不然客户端会等待server的数据过来，
				// 直到socket超时，导致数据不完整。
				fis.close();
				dos.close();
				s.close();
				System.out.println("文件传输完成");

			} catch (Exception e) {
				e.printStackTrace();
				callBack.error(e);
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		public void close() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.close();
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
		 * 设备已连接
		 *
		 * @param ip
		 * @param name
		 */
		void isConnected(String ip, String name);

		/**
		 * 关闭
		 */
		void close();

		/**
		 * 抛异常了
		 */
		void error(Exception e);
	}
}
