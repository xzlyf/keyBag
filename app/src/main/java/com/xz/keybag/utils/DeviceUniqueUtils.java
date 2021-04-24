package com.xz.keybag.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/23
 * <p>
 * 获取设备唯一标识
 */
public class DeviceUniqueUtils {

	/**
	 * 获取设备唯一标识
	 * 先获取imei 获取失败再获取 sn 获取失败再随机生成一个uuid
	 *
	 * @param context
	 * @return
	 */
	public static String getPhoneSign(Context context) {
		StringBuilder deviceId = new StringBuilder();
		// 渠道标志
		try {
			//IMEI（imei）
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (!TextUtils.isEmpty(imei)) {
				deviceId.append("imei_");
				deviceId.append(imei);
				return deviceId.toString();
			}
			//序列号（sn）
			String sn = tm.getSimSerialNumber();
			if (!TextUtils.isEmpty(sn)) {
				deviceId.append("sn_");
				deviceId.append(sn);
				return deviceId.toString();
			}
			//如果上面都没有， 则生成一个id：随机码
			String uuid = UUIDUtil.getStrUUID();
			if (!TextUtils.isEmpty(uuid)) {
				deviceId.append("id_");
				deviceId.append(uuid);
				return deviceId.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			deviceId.append("id").append(UUIDUtil.getStrUUID());
		}
		return deviceId.toString();
	}
}
