package com.xz.keybag.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;

import com.xz.keybag.entity.AppInfo;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/19
 */
public class AppInfoUtils {
	/**
	 * 获取包签名MD5
	 * 适配 android P
	 */
	public static String getPackageSign(Context context, boolean isMd5) {
		String signStr = "-1";
		if (context != null) {
			//获取包管理器
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo;
			//获取当前要获取 SHA1 值的包名，也可以用其他的包名，但需要注意，
			//在用其他包名的前提是，此方法传递的参数 Context 应该是对应包的上下文。
			String packageName = context.getPackageName();
			//签名信息
			Signature[] signatures = null;
			try {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {

					packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
					SigningInfo signingInfo = packageInfo.signingInfo;
					signatures = signingInfo.getApkContentsSigners();
				} else {
					//获得包的所有内容信息类
					packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
					signatures = packageInfo.signatures;
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			if (null != signatures && signatures.length > 0) {
				Signature sign = signatures[0];
				if (!isMd5) {
					return signatures[0].toCharsString();
				}
				signStr = encryptionMD5(sign.toByteArray()).toUpperCase();
			}
		}
		return signStr;
	}

	private static String encryptionMD5(byte[] byteStr) {
		MessageDigest messageDigest;
		StringBuffer md5StrBuff = new StringBuffer();
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(byteStr);
			byte[] byteArray = messageDigest.digest();
			for (byte aByteArray : byteArray) {
				if (Integer.toHexString(0xFF & aByteArray).length() == 1) {
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5StrBuff.toString();
	}


	/**
	 * 获取所有应用列表包名
	 *
	 * @param isFilterSys 是否过滤系统应用
	 */
	public static List<AppInfo> getAllApp(Context context, boolean isFilterSys) {
		List<AppInfo> list = new ArrayList<>();
		try {
			PackageManager pm = context.getPackageManager();
			List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES |
					PackageManager.GET_SERVICES);
			AppInfo appInfo;
			for (PackageInfo info : packageInfos) {
				appInfo = new AppInfo();
				appInfo.setPackageName(info.packageName);
				appInfo.setAppName(info.applicationInfo.loadLabel(pm).toString());
				appInfo.setIcon(info.applicationInfo.loadIcon(pm));
				if (isFilterSys) {
					//如果不是系统应用，则添加至appList
					if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						list.add(appInfo);
					}
				} else {
					//不过滤系统应用
					list.add(appInfo);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过包名获取app名
	 */
	public static String getAppName(String packageName, Context context) {
		PackageManager pm = context.getPackageManager();
		String Name;
		try {
			Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
		} catch (PackageManager.NameNotFoundException e) {
			Name = "";
		}
		return Name;
	}
}
