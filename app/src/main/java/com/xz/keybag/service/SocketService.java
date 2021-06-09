package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.DBManager;
import com.xz.keybag.utils.AppInfoUtils;
import com.xz.keybag.utils.IOUtil;
import com.xz.keybag.utils.StorageUtil;
import com.xz.keybag.utils.lock.RSA;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class SocketService extends Service {
	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private RunningThread runningThread = null;

	private String ip = "192.168.1.37";// 设置成服务器IP

	private int port = 20022;
	private String cachePath = "";
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
			runningThread = null;
		}
	}


	private class RunningThread extends Thread {
		@Override
		public void run() {
			callBack.message("正在尝试连接...");
			deployClient();
			makeProject();
			clearCache();
			callBack.finish();
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
				callBack.message("连接失败");
				callBack.connectFailed();
				releaseSocket();
				return;
			}

			callBack.message("连接成功");
			callBack.isConnected();
			//输出验证信息 公共RSA(版本号@时间戳)
			DataOutputStream dos = null;
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				StringBuilder verifyBuff = new StringBuilder();
				verifyBuff.append(AppInfoUtils.getVersionCode(SocketService.this));
				verifyBuff.append(Local.PROTOCOL_SPLIT);
				verifyBuff.append(System.currentTimeMillis());
				dos.writeUTF(RSA.publicEncrypt(verifyBuff.toString(), RSA.getPublicKey(Local.publicKey)));
			} catch (Exception e) {
				e.printStackTrace();
				IOUtil.closeAll(dos, socket);
				callBack.message("验证失败");
				callBack.verifyFailed();
				return;
			}
			callBack.message("验证成功");
			callBack.message("正在接收文件...");
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

				//callBack.message("文件的长度为:" + len + "\n");
				callBack.message("开始接收!" + "\n");

				while (true) {
					int read = 0;
					if (dis != null) {
						read = dis.read(buf);
					}
					passedlen += read;
					if (read == -1) {
						break;
					}
					callBack.message("接收了" + (passedlen * 100 / len) + "%\n");
					fileOut.write(buf, 0, read);
				}
				//callBack.message("接收完成，文件存为" + savePath + "\n");
				cachePath = savePath;
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				callBack.message("接收消息错误" + "\n");
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
			callBack.message("正在整合文件...");

			//读取缓存文件
			FileReader fileReader;
			StringBuilder sBuff;
			try {
				fileReader = new FileReader(cacheFile);
				char[] buf = new char[1024];
				int num;
				sBuff = new StringBuilder();
				while ((num = fileReader.read(buf)) != -1) {
					//字符异或处理
					for (int i = 0; i < buf.length; i++) {
						buf[i] = (char) (buf[i] ^ 8);
					}
					sBuff.append(buf, 0, num);
				}
			} catch (IOException e) {
				e.printStackTrace();
				callBack.message("缓存文件异常\n结束");
				return;
			}

			//解析json数据 并写入数据库
			Gson gson = new Gson();
			List<Project> list = gson.fromJson(sBuff.toString(), new TypeToken<List<Project>>() {
			}.getType());
			for (Project p : list) {
				db.insertProject(p);
			}

			callBack.message("数据整合成功");

		}

		/**
		 * 清理缓存文件
		 */
		private void clearCache() {
			File cacheFile = new File(cachePath);
			if (!cacheFile.exists()) {
				return;
			}
			boolean delete = cacheFile.delete();
			callBack.message(delete ? "清理缓存...成功" : "清理缓存...失败");
		}
	}


	/**
	 * 回调做回到主线处理
	 */
	private SocketCallBack callBack = new SocketCallBack() {


		@Override
		public void isConnected() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.isConnected();
				}
			});
		}

		@Override
		public void connectFailed() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.connectFailed();
				}
			});
		}

		@Override
		public void verifyFailed() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mCallback.verifyFailed();
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
		 * 设备已连接
		 */
		void isConnected();

		/**
		 * 连接失败
		 */
		void connectFailed();

		/**
		 * 验证失败
		 */
		void verifyFailed();

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
