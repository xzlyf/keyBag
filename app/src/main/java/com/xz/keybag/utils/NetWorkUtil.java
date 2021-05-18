package com.xz.keybag.utils;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/5/17
 */
public class NetWorkUtil {

	/**
	 * 获取连接情况
	 *
	 * @return 以下列举常见返回 详情参考 {@link ConnectivityManager#TYPE_MOBILE}
	 * -1 没有连接
	 * 0 移动数据
	 * 1 WIFI
	 * 7 蓝牙数据
	 * 9 以太网
	 */
	@RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 获取本机ip
	 * 连接wifi情况下
	 *
	 * @return null wifi没有启用
	 */
	@RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
	public static String getIpInWifi(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			return null;
		}
		WifiInfo info = wifiManager.getConnectionInfo();
		int ipAddress = info.getIpAddress();
		return intToIp(ipAddress);
	}

	/**
	 * 获取本机ip
	 * 使用GPRS情况下
	 *
	 * @return null
	 */
	public static String getIpInGPRS() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	/**
	 * 整数转字符串ip
	 *
	 * @param i
	 * @return
	 */
	private static String intToIp(int i) {
		return (i & 0xFF) + "." +
				((i >> 8) & 0xFF) + "." +
				((i >> 16) & 0xFF) + "." +
				(i >> 24 & 0xFF);
	}
}
