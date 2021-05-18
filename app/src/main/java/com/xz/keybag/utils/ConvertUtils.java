package com.xz.keybag.utils;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/5/15
 */
public class ConvertUtils {

	/**
	 * 字符串转换成Long  转换失败返回默认值
	 *
	 * @param st          原文
	 * @param defaultLong 默认值
	 */
	public static long convertStingToLong(String st, long defaultLong) {
		try {
			return Long.parseLong(st);
		} catch (NumberFormatException e) {
			return defaultLong;
		}
	}
}
