package com.xz.keybag.utils;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2020/12/10
 */
public class ColorUtil {

	public static String int2Hex(int colorInt) {
		String hexCode = "";
		hexCode = String.format("#%06X", Integer.valueOf(16777215 & colorInt));
		return hexCode;
	}
}
