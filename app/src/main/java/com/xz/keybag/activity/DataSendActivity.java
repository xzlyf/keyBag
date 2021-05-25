package com.xz.keybag.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.service.ServerSocketService;
import com.xz.keybag.utils.NetWorkUtil;
import com.xz.keybag.utils.ZxingUtils;
import com.xz.keybag.utils.lock.RSA;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.OnClick;

public class DataSendActivity extends BaseActivity {

	@BindView(R.id.qr_layout)
	CardView qrLayout;
	@BindView(R.id.image_qr)
	ImageView imageQr;
	@BindView(R.id.tv_log)
	TextView tvLog;

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

	@OnClick(R.id.tv_back)
	public void onViewClick(View v) {
		switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
		}
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
		//获取当前ip
		String host = checkConnectType();
		if (host == null) {
			return;
		}
		Logger.d("服务端ip：" + host);
		socketService.setCallback(new ServerSocketService.SocketCallBack() {
			@Override
			public void created(int port) {
				//服务器部署成功
				Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.lanuch_max);
				Bitmap qrCode = ZxingUtils.createImage(getQrCode(host, port), 400, 400, logo);
				Glide.with(mContext).asBitmap().load(qrCode).into(imageQr);
			}

			@Override
			public void isConnected(String ip, String name) {
				//客户端已连接
				qrLayout.setVisibility(View.GONE);
				appendLog("已连接:" + ip);
				appendLog("设备:" + name);
			}

			@Override
			public void close() {

			}

			@Override
			public void error(Exception e) {

			}
		});
		socketService.initSocket();
	}

	/**
	 * 检查网络状态是否符合
	 *
	 * @return 如果符合返回当前Ip
	 */
	public String checkConnectType() {
		int connectedType = NetWorkUtil.getConnectedType(mContext);
		if (connectedType == 1) {
			//网络状态符合 wifi
			return NetWorkUtil.getIpInWifi(mContext);
		}
		//网络状态不符合
		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setMessage("请先开启WIFI\n确保与接收方在同一个WIFi下")
				.setPositiveButton("开启wifi", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//跳转到配置wifi界面
						startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				})
				.create();
		dialog.show();

		return null;
	}


	/**
	 * 生成二维码文本数据
	 * 根据二维码协议
	 * 格式：RSA(头协议@ip@port)
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

	/**
	 * 输出log
	 * 自动滚动到底部
	 */
	private void appendLog(String st) {
		tvLog.append(st);
		tvLog.append("\n");
		//滚动到底部
		tvLog.scrollTo(0, tvLog.getLineCount() * tvLog.getLineHeight() - (tvLog.getHeight() - (tvLog.getPaddingTop() * 2)));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(sc);
		stopService(serverIntent);
	}

}
