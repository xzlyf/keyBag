package com.xz.keybag.utils;

import android.content.Context;

import com.xz.apache_code_android.binary.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class FileTool {
	public static void save(Context context, String child, String data) {
		String path = StorageUtil.getDataDir(context);
		File file = new File(path, child);
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		try {
			if (!file.exists()) {
				boolean isCreate = file.createNewFile();
				if (!isCreate) {
					throw new IOException("File create error");
				}
			}

			osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			bw = new BufferedWriter(osw);
			//bw.write(Base64.encodeBase64String(data.getBytes()));
			bw.write(encrypt(data));
			bw.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeAll(osw, bw);
		}

	}

	public static String read(Context context, String child) {
		String path = StorageUtil.getDataDir(context);
		File file = new File(path, child);
		BufferedReader in = null;
		try {
			if (!file.exists()) {
				return null;
			}

			in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(file), StandardCharsets.UTF_8));

			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}

			//return new String(Base64.decodeBase64(sb.toString()));
			return decrypt(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeAll(in);
		}
		return null;
	}

	public static boolean delete(Context context, String child) {
		String path = StorageUtil.getDataDir(context);
		File file = new File(path, child);
		if (file.exists()) {
			return file.delete();
		}
		return false;

	}


	private static final int RADIX = 16;
	private static final String SEED = "0933910847463829232312312";

	/**
	 * 异或加密
	 */
	private static String encrypt(String password) {
		if (password == null)
			return "";
		if (password.length() == 0)
			return "";

		BigInteger bi_passwd = new BigInteger(password.getBytes());

		BigInteger bi_r0 = new BigInteger(SEED);
		BigInteger bi_r1 = bi_r0.xor(bi_passwd);

		return bi_r1.toString(RADIX);
	}

	/**
	 * 异或解密
	 */
	private static String decrypt(String encrypted) {
		if (encrypted == null)
			return "";
		if (encrypted.length() == 0)
			return "";

		BigInteger bi_confuse = new BigInteger(SEED);

		try {
			BigInteger bi_r1 = new BigInteger(encrypted, RADIX);
			BigInteger bi_r0 = bi_r1.xor(bi_confuse);

			return new String(bi_r0.toByteArray());
		} catch (Exception e) {
			return "";
		}
	}

}
