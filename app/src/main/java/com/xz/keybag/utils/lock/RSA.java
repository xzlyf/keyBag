package com.xz.keybag.utils.lock;


import android.util.Log;

import com.xz.apache_code_android.binary.Base64;
import com.xz.keybag.utils.IOUtil;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA
 * 私钥加密-公钥解密
 * 私钥解密-公钥加密
 * <p>
 * 获得公钥
 * keyMap.get("publicKey");
 * 获得私钥
 * keyMap.get("privateKey");
 */
public class RSA {
	public static final String CHARSET = "UTF-8";
	public static final String RSA_ALGORITHM = "RSA"; // ALGORITHM ['ælgərɪð(ə)m] 算法的意思
	public static final String RSA = "RSA/ECB/PKCS1Padding";

	/*// 简单测试____________
	public static void main(String[] args) throws Exception {
		Map<String, String> keyMap = createKeys(1024);
		String publicKey = keyMap.get("publicKey");
		String privateKey = keyMap.get("privateKey");
		System.out.println("公钥: \n\r" + publicKey);
		System.out.println("私钥： \n\r" + privateKey);


		String message = "123456abc";
		System.out.println("明文数据========" + message);
		System.out.println("======================================================================");
		String privateStr = privateEncrypt(message, getPrivateKey(privateKey));//传入明文和公钥加密,得到密文
		System.out.println("私钥加密========" + privateKey);
		String publicStr = publicDecrypt(privateStr, getPublicKey(publicKey)); //传入密文和私钥,得到明文
		System.out.println("公钥解密========" + publicStr);
		System.out.println("======================================================================");
		String publicStr2 = publicEncrypt(message, getPublicKey(publicKey));
		System.out.println("公钥加密========" + publicStr2);
		String privateStr2 = privateDecrypt(publicStr2, getPrivateKey(privateKey));
		System.out.println("私钥加密========" + privateStr2);
	}*/

	public static Map<String, String> createKeys(int keySize) {
		// 为RSA算法创建一个KeyPairGenerator对象
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			Log.e("RSA", "No such algorithm-->[\" + RSA_ALGORITHM + \"]");
			return null;
		}

		// 初始化KeyPairGenerator对象,密钥长度
		kpg.initialize(keySize);
		// 生成密匙对
		KeyPair keyPair = kpg.generateKeyPair();
		// 得到公钥
		Key publicKey = keyPair.getPublic();
		String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
		// 得到私钥
		Key privateKey = keyPair.getPrivate();
		String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
		// map装载公钥和私钥
		Map<String, String> keyPairMap = new HashMap<String, String>();
		keyPairMap.put("publicKey", publicKeyStr);
		keyPairMap.put("privateKey", privateKeyStr);
		// 返回map
		return keyPairMap;
	}

	/**
	 * 得到公钥
	 * 传入密文和私钥,得到明文
	 *
	 * @param publicKey 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通过X509编码的Key指令获得公钥对象
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
		return key;
	}

	/**
	 * 得到私钥
	 * 传入明文和公钥加密,得到密文
	 *
	 * @param privateKey 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通过PKCS#8编码的Key指令获得私钥对象
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		return key;
	}

	/**
	 * 公钥加密
	 *
	 * @param data
	 * @param publicKey
	 * @return
	 */
	public static String publicEncrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	/**
	 * 私钥解密
	 *
	 * @param data
	 * @param privateKey
	 * @return
	 */

	public static String privateDecrypt(String data, RSAPrivateKey privateKey) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new Exception("解密字符串[" + data + "]时遇到异常", e);
		}
	}

	/**
	 * 私钥加密
	 *
	 * @param data
	 * @param privateKey
	 * @return
	 */

	public static String privateEncrypt(String data, RSAPrivateKey privateKey) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			//每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new Exception("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	/**
	 * 公钥解密
	 *
	 * @param data
	 * @param publicKey
	 * @return
	 */

	public static String publicDecrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
		}
	}

	//rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;  //最大块
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			while (datas.length > offSet) {
				if (datas.length - offSet > maxBlock) {
					//可以调用以下的doFinal（）方法完成加密或解密数据：
					buff = cipher.doFinal(datas, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(datas, offSet, datas.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
		byte[] resultDatas = out.toByteArray();
		IOUtil.closeAll(out);
		return resultDatas;
	}


}