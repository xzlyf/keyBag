package com.xz.keybag.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/1/23
 */
public class IOUtil {

	/**
	 * 流关闭
	 * @param cs
	 */
	public static void closeAll(Closeable... cs) {
		// 遍历数组
		if (cs != null) {
			for (Closeable c : cs) {
				// 只要传进来的不是空的都给它把流关闭
				if (c != null) {
					try {
						c.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
