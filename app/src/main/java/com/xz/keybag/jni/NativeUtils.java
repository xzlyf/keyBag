package com.xz.keybag.jni;

import android.content.Context;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/17
 */
public class NativeUtils {

	static {
		System.loadLibrary("native");
	}

	/**
	 * 校验包名和签名
	 */
	public static native void signatureVerify(Context context,boolean isDebug);

	/**
	 * 传入拼接好的参数进行签名加密
	 * @return 已加密的签名
	 */
	public static native String signatureParams(String params);
}
