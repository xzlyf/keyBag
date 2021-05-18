package com.xz.keybag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.orhanobut.logger.Logger;
import com.xz.keybag.utils.IOUtil;
import com.xz.keybag.utils.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SocketService extends Service {
	private final int TIME_OUT_CONNECT = 5 * 1000;
	private SocketBinder socketBinder = new SocketBinder();
	private SocketCallBack mCallback;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private ServerDeployThread mServerThread = null;
	private Socket mClient;


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
		if (mClient == null && mServerThread == null) {
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
				mClient = new Socket();
				//mClient.connect(new InetSocketAddress("10.0.2.2", 20022), TIME_OUT_CONNECT);
				mClient.connect(new InetSocketAddress("192.168.1.66", 12345), TIME_OUT_CONNECT);
				// 客户端socket在接收数据时，有两种超时：1. 连接服务器超时，即连接超时；2. 连接服务器成功后，接收服务器数据超时，即接收超时
				// 设置 socket 读取数据流的超时时间
				//mClient.setSoTimeout(5000);
				// 发送数据包，默认为 false，即客户端发送数据采用 Nagle 算法；
				// 但是对于实时交互性高的程序，建议其改为 true，即关闭 Nagle 算法，客户端每发送一次数据，无论数据包大小都会将这些数据发送出去
				//mClient.setTcpNoDelay(true);
				// 设置客户端 socket 关闭时，close() 方法起作用时延迟 30 秒关闭，如果 30 秒内尽量将未发送的数据包发送出去
				//mClient.setSoLinger(true, 30);
				// 设置输出流的发送缓冲区大小，默认是4KB，即4096字节
				//mClient.setSendBufferSize(4096);
				// 设置输入流的接收缓冲区大小，默认是4KB，即4096字节
				//mClient.setReceiveBufferSize(4096);
				// 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
				// 防止服务器端无效时，客户端长时间处于连接状态
				//mClient.setKeepAlive(true);
				// 代表可以立即向服务器端发送单字节数据
				//mClient.setOOBInline(true);
				// 数据不经过输出缓冲区，立即发送
				//mClient.sendUrgentData(0x44);//"D"
				if (!mClient.isConnected()) {
					Logger.e("连接失败");
					return;
				}
				Logger.w("连接成功");
				sendOrder("hi，我是客户端");

				// TODO: 2021/5/18 还是完全解决两台虚拟机互联的问题，目前通过修改端口映射可以实现Connected，但是传输不了数据，连接了但是没有完全连接上
			} catch (IOException e) {
				Logger.e("ServerSocketDeploy Error " + e.getMessage());
			}


		}
	}

	private final int ST_LENGTH = 1024;
	private OutputStream mOutStream;

	private void sendOrder(String st) throws IOException {
		if (mOutStream == null) {
			if (mClient.isConnected()) {
				mOutStream = mClient.getOutputStream();
			} else {
				Logger.e("连接已关闭");
				return;
			}
		}
		//数据长度超过1024字自动切割，分批发送
		List<String> dataList = StringUtil.getStrList(st, ST_LENGTH);
		for (int i = 0; i < dataList.size(); i++) {
			mOutStream.write(dataList.get(i).getBytes(StandardCharsets.UTF_8));
			mOutStream.flush();
		}

		mOutStream.close();

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
