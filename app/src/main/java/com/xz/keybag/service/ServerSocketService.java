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
import com.xz.keybag.utils.IOUtil;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class ServerSocketService extends Service {

	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

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
		if (runningThread != null) {
			runningThread.interrupt();
		}
	}


	private class RunningThread extends Thread {
		int reTryNum = 0;

		@Override
		public void run() {
			callBack.message("正在整理数据...");
			createCache();
			callBack.message("正在部署服务端...");
			deploySocket(port);
			clearCache();
			callBack.message("传输完成，已关闭连接");
		}

		/**
		 * 请里缓存文件
		 */
		private void clearCache() {
			File file = new File(cachePath);
			if (file.exists()) {
				file.delete();
				callBack.message("缓存文件已清除");
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
				//for (Project p : projects) {
				//	buff = gson.toJson(p).getBytes();
				//	fos.write(buff);
				//}
				buff = gson.toJson(projects).getBytes(StandardCharsets.UTF_8);
				//位移处理，不要明文输出文本
				for (int i = 0; i < buff.length; i++) {
					//buff[i] += 1;
					buff[i] = (byte) (buff[i] << 1);
				}
				fos.write(buff);
				fos.flush();

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
			ServerSocket ss;

			//创建服务端
			try {
				ss = new ServerSocket(port);
			} catch (IOException e) {
				if (e instanceof BindException) {
					if (reTryNum >= 3) {
						callBack.message("超过重试次数，结束部署");
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
				callBack.message("服务器部署异常：" + e.getMessage());
				return;
			}
			callBack.message("服务端已部署,开始等待接入...");
			callBack.created(ss.getLocalPort());


			//等待客户端接入
			Socket s = null;
			DataInputStream dis = null;
			try {
				while (true) {
					s = ss.accept();
					callBack.message("有设备已接入");
					dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
					String verify = dis.readUTF();
					//校验信息
					String[] split = verify.split("@");
					if (split.length == 1) {
						callBack.message("设备验证不通过\n重新等待接入...");
						dis.close();
						s.close();
					} else {
						InetAddress inetAddress = s.getInetAddress();
						callBack.isConnected(inetAddress.getHostAddress(), inetAddress.getHostName());
						ss.close();//关闭服务器等待
						break;
					}
				}
			} catch (IOException e) {
				callBack.message("结束等待");
				return;
			}
			callBack.message("开始发送数据，请勿退出或息屏");
			callBack.message("......");
			// 选择进行传输的文件
			DataInputStream fis = null;
			DataOutputStream dos = null;
			try {
				File fi = new File(cachePath);
				fis = new DataInputStream(new BufferedInputStream(new FileInputStream(cachePath)));
				dos = new DataOutputStream(s.getOutputStream());
				//将文件名及长度传给客户端。这里要真正适用所有平台，例如中文名的处理，还需要加工，具体可以参见Think In Java 4th里有现成的代码。
				dos.writeUTF(fi.getName());
				dos.flush();
				dos.writeLong(fi.length());
				dos.flush();

				int bufferSize = 2048;
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
				callBack.message("文件传输完成");
				callBack.finish();

			} catch (Exception e) {
				e.printStackTrace();
				callBack.error(e);
			} finally {
				IOUtil.closeAll(dos, fis, dis, s);
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
		public void message(String msg) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.message(msg);
				}
			});
		}

		@Override
		public void finish() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.finish();
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
		 * 消息
		 */
		void message(String msg);

		/**
		 * 完成
		 */
		void finish();


		/**
		 * 抛异常了
		 */
		void error(Exception e);
	}
}
