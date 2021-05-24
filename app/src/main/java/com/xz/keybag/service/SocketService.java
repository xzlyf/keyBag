package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.xz.keybag.utils.StorageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.Socket;

public class SocketService extends Service {
	private final int TIME_OUT_CONNECT = 5 * 1000;
	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private RunningThread runningThread = null;
	private ClientSocket cs = null;

	private String ip = "192.168.1.37";// 设置成服务器IP

	private int port = 20022;


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
		if (cs != null) {
			cs.shutDownConnection();
		}
		if (runningThread != null) {
			runningThread.interrupt();
		}
	}


	private class RunningThread extends Thread {
		@Override
		public void run() {
			//生成校验数据: RSA(app版本@android系统版本)
			String verify = "1asdmakshdk@samdkajdoi21";

			try {
				if (createConnection()) {
					sendMessage(verify);
					getMessage();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	private boolean createConnection() {
		cs = new ClientSocket(ip, port);
		try {
			cs.CreateConnection();
			System.out.print("连接服务器成功!" + "\n");
			return true;
		} catch (Exception e) {
			System.out.print("连接服务器失败!" + "\n");
			return false;
		}

	}

	private void sendMessage(String message) {
		if (cs == null)
			return;
		try {
			cs.sendMessage(message);
		} catch (Exception e) {
			System.out.print("发送消息失败!" + "\n");
		}
	}

	private void getMessage() {
		if (cs == null)
			return;
		DataInputStream inputStream = null;
		try {
			inputStream = cs.getMessageStream();
		} catch (Exception e) {
			System.out.print("接收消息缓存错误\n");
			return;
		}

		try {
			//本地保存路径，文件名会自动从服务器端继承而来。
			String savePath = StorageUtil.getCacheDir(SocketService.this).getAbsolutePath() + "/";
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			int passedlen = 0;
			long len = 0;

			savePath += inputStream.readUTF();
			DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
			len = inputStream.readLong();

			System.out.println("文件的长度为:" + len + "\n");
			System.out.println("开始接收文件!" + "\n");

			while (true) {
				int read = 0;
				if (inputStream != null) {
					read = inputStream.read(buf);
				}
				passedlen += read;
				if (read == -1) {
					break;
				}
				//下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
				System.out.println("文件接收了" + (passedlen * 100 / len) + "%\n");
				fileOut.write(buf, 0, read);
			}
			System.out.println("接收完成，文件存为" + savePath + "\n");

			fileOut.close();
		} catch (Exception e) {
			System.out.println("接收消息错误" + "\n");
			e.printStackTrace();
			return;
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

	private class ClientSocket {
		private String ip;

		private int port;

		private Socket socket = null;

		DataOutputStream out = null;

		DataInputStream getMessageStream = null;

		public ClientSocket(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		/**
		 * 创建socket连接
		 *
		 * @throws Exception exception
		 */
		public void CreateConnection() throws Exception {
			try {
				socket = new Socket(ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				if (socket != null)
					socket.close();
				throw e;
			} finally {
			}
		}

		public void sendMessage(String sendMessage) throws Exception {
			try {
				out = new DataOutputStream(socket.getOutputStream());
				if (sendMessage.equals("Windows")) {
					out.writeByte(0x1);
					out.flush();
					return;
				}
				if (sendMessage.equals("Unix")) {
					out.writeByte(0x2);
					out.flush();
					return;
				}
				if (sendMessage.equals("Linux")) {
					out.writeByte(0x3);
					out.flush();
				} else {
					out.writeUTF(sendMessage);
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (out != null)
					out.close();
				throw e;
			} finally {
			}
		}

		public DataInputStream getMessageStream() throws Exception {
			try {
				getMessageStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				return getMessageStream;
			} catch (Exception e) {
				e.printStackTrace();
				if (getMessageStream != null)
					getMessageStream.close();
				throw e;
			} finally {
			}
		}

		public void shutDownConnection() {
			try {
				if (out != null)
					out.close();
				if (getMessageStream != null)
					getMessageStream.close();
				if (socket != null)
					socket.close();
			} catch (Exception e) {

			}
		}
	}
}
