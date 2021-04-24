package com.xz.keybag.utils.lock;

import android.util.Log;

import com.xz.apache_code_android.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES工具类
 *
 *
 *   因为某些国家的进口管制限制，Java发布的运行环境包中的加解密有一定的限制。比如默认不允许256位密钥的AES加解密，解决方法就是修改策略文件。
 *   替换的文件：%JDK_HOME%\jre\lib\security\local_policy.jar
 * 参考： http://czj4451.iteye.com/blog/1986483
 */
public class AES {
    /*public static void main(String[] args) throws Exception {
        String s = "hello world";
        // 加密
        System.out.println("加密前：" + s);
        String encryptResultStr = encrypt(s);
        System.out.println("加密后：" + encryptResultStr);
        // 解密
        System.out.println("解密后：" + decrypt(encryptResultStr));
    }*/

    // 密钥
    public static String key = "AD42F6697B035B7580E4FEF93BE20BAD";
    private static String charset = "utf-8";
    // 偏移量
    private static int offset = 16;
    private static String transformation = "AES/CBC/PKCS5Padding";
    private static String algorithm = "AES";

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        return encrypt(content, key);
    }

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return decrypt(content, key);
    }

    /**
     * 加密
     *
     * @param content
     *            需要加密的内容
     * @param key
     *            加密密码
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] byteContent = content.getBytes(charset);
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return new Base64().encodeToString(result); // 加密
        } catch (Exception e) {
            Log.e("AES", "encrypt: "+e.getMessage() );
        }
        return null;
    }

    /**
     * AES（256）解密
     *
     * @param content
     *            待解密内容
     * @param key
     *            解密密钥
     * @return 解密之后
     * @throws Exception
     */
    public static String decrypt(String content, String key) {
        try {

            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化
            byte[] result = cipher.doFinal(new Base64().decode(content));
            return new String(result); // 解密
        } catch (Exception e) {
            Log.e("AES", "decrypt: "+e.getMessage() );
        }
        return null;
    }

}