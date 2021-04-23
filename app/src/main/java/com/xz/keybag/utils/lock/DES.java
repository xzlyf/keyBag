package com.xz.keybag.utils.lock;

import com.xz.apache_code_android.binary.Base64;
import com.xz.apache_code_android.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DES {

	/*public static void main(String[] args) {
		String st = "hello world";
		DES des = new DES();
		System.out.println("=====================");
		System.out.println("st=" + st);
		String secret = des.getKey();
		System.out.println("secret=" + secret);
		String encrySt = des.encryptor(st, secret);
		System.out.println("encrySt=" + encrySt);
		String decrySt = des.decryptor(encrySt, secret);
		System.out.println("decrySt=" + decrySt);


	}*/


	//生成一个DES密钥
	public static String getKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
			keyGenerator.init(56);
			// 生成一个Key
			SecretKey generateKey = keyGenerator.generateKey();
			// 转变为字节数组
			byte[] encoded = generateKey.getEncoded();
			// 生成密钥字符串
			return Hex.encodeHexString(encoded);
		} catch (Exception e) {
			e.printStackTrace();
			return "error_getKey";
		}
	}

	//加密
	public static String encryptor(String str, String Key) {
		String s = null;
		try {
			DESKeySpec desKey = new DESKeySpec(Key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey);    //初始化密码器，用密钥 secretKey 进入加密模式
			byte[] bytes = cipher.doFinal(str.getBytes());   //加密
			s = Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return "error_encrypt.";
		}
		return s;
	}

	//解密
	public static String decryptor(String buff, String Key) {
		String s = null;
		try {
			DESKeySpec desKey = new DESKeySpec(Key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, securekey);
			byte[] responseByte = cipher.doFinal(Base64.decodeBase64(buff));
			s = new String(responseByte);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return "error_decrypt.";
		}
	}
}
