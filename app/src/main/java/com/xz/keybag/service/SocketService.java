package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.keybag.utils.AppInfoUtils;
import com.xz.keybag.utils.IOUtil;
import com.xz.keybag.utils.StorageUtil;
import com.xz.keybag.utils.lock.RSA;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class SocketService extends Service {
	private final int TIME_OUT_CONNECT = 5 * 1000;
	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private RunningThread runningThread = null;

	private String ip = "192.168.1.37";// 设置成服务器IP

	private int port = 20022;
	private String cachePath;
	private DBManager db;


	public SocketService() {
		db = DBManager.getInstance(this);
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
	public void initSocket(String ip, int port) {
		if (runningThread == null) {
			this.ip = ip;
			this.port = port;
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
		@Override
		public void run() {
			System.out.println("正在尝试连接...");
			deployClient();
			makeProject();
			clearCache();
		}


		/**
		 * 部署客户端
		 */
		private void deployClient() {
			Socket socket = null;
			try {
				socket = new Socket(ip, port);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("连接失败");
				return;
			}

			System.out.println("连接成功");

			//输出验证信息 公共RSA(版本号@时间戳)
			DataOutputStream dos = null;
			try {
				dos = new DataOutputStream(socket.getOutputStream());

				StringBuilder verifyBuff = new StringBuilder();
				verifyBuff.append(AppInfoUtils.getVersionCode(SocketService.this));
				verifyBuff.append(Local.PROTOCOL_SPLIT);
				verifyBuff.append(System.currentTimeMillis());
				dos.writeUTF(RSA.publicEncrypt(verifyBuff.toString(), RSA.getPublicKey(Local.publicKey)));
			} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
				IOUtil.closeAll(dos, socket);
				System.out.println("验证失败");
				return;
			}
			System.out.println("验证成功");


			System.out.println("正在接收文件...");
			//开始接收数据
			DataInputStream dis = null;
			DataOutputStream fileOut = null;
			try {
				dis = new DataInputStream(socket.getInputStream());
				//本地保存路径，文件名会自动从服务器端继承而来。
				String savePath = StorageUtil.getCacheDir(SocketService.this).getAbsolutePath() + File.separator;
				int bufferSize = 2048;
				byte[] buf = new byte[bufferSize];
				int passedlen = 0;
				long len = 0;

				savePath += dis.readUTF();
				fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
				len = dis.readLong();

				System.out.println("文件的长度为:" + len + "\n");
				System.out.println("开始接收文件!" + "\n");

				while (true) {
					int read = 0;
					if (dis != null) {
						read = dis.read(buf);
					}
					passedlen += read;
					if (read == -1) {
						break;
					}
					System.out.println("文件接收了" + (passedlen * 100 / len) + "%\n");
					fileOut.write(buf, 0, read);
				}
				System.out.println("接收完成，文件存为" + savePath + "\n");
				cachePath = savePath;
				fileOut.close();
			} catch (Exception e) {
				System.out.println("接收消息错误" + "\n");
			} finally {
				IOUtil.closeAll(dos, dis, fileOut, socket);
			}
		}

		/**
		 * 写入数据库
		 */
		private void makeProject() {
			File cacheFile = new File(cachePath);
			if (!cacheFile.exists()) {
				return;
			}
			System.out.println("正在整合文件...");

			FileInputStream fis;
			StringBuilder buff = null;

			try {
				fis = new FileInputStream(cacheFile);
				int n = 0;
				buff = new StringBuilder();
				while (n != -1) {
					n = fis.read();
					buff.append((char) (n ^ 8));
				}

				Logger.json(buff.toString());

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("缓存文件异常\n结束");
				return;
			}

			//Gson gson = new Gson();
			//List<Project> list = gson.fromJson(buff.toString(), new TypeToken<List<Project>>() {
			//}.getType());
			//Logger.d("长度:" + list.size());

		}

		private void clearCache() {
			File cacheFile = new File(cachePath);
			if (!cacheFile.exists()) {
				return;
			}
			boolean delete = cacheFile.delete();
			System.out.println(delete ? "清理缓存...成功" : "清理缓存...失败");
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
