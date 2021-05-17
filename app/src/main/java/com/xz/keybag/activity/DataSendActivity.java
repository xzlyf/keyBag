package com.xz.keybag.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.bumptech.glide.Glide;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.service.ServerSocketService;
import com.xz.keybag.utils.ZxingUtils;
import com.xz.keybag.utils.lock.RSA;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DataSendActivity extends BaseActivity {

	private ServiceConnection sc;
	public ServerSocketService socketService;
	private Intent serverIntent;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_data_send;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		bindSocketService();
	}

	private void bindSocketService() {
		/*通过binder拿到service*/
		sc = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
				ServerSocketService.SocketBinder binder = (ServerSocketService.SocketBinder) iBinder;
				socketService = binder.getService();
				initServerSocket();
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {

			}
		};
		serverIntent = new Intent(getApplicationContext(), ServerSocketService.class);
		bindService(serverIntent, sc, BIND_AUTO_CREATE);
	}


	private void initServerSocket() {
		socketService.setCallback(new ServerSocketService.SocketCallBack() {
			@Override
			public void created(int port) {
				//Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.lanuch_max);
				//Bitmap qrCode = ZxingUtils.createImage(qrSt, 400, 400, logo);
				//Glide.with(this).asBitmap().load(qrCode).into(imageQr);
			}

			@Override
			public void error(Exception e) {

			}
		});
		//socketService.initSocket();
	}


	/**
	 * 生成二维码文本数据
	 * 根据二维码协议
	 * 格式：协议头@ip@端口
	 */
	private String getQrCode(String ip, int port) {
		StringBuilder qrSt = new StringBuilder();
		try {
			qrSt.append(Local.PROTOCOL_QR_SHARD);
			qrSt.append(Local.PROTOCOL_SPLIT);
			qrSt.append(ip);
			qrSt.append(Local.PROTOCOL_SPLIT);
			qrSt.append(port);
			return RSA.publicEncrypt(qrSt.toString(), RSA.getPublicKey(Local.publicKey));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			sToast("密钥文件已被损坏");
			return "error_failure";
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		stopService(serverIntent);
	}

}
