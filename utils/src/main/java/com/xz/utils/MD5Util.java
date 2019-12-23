package com.xz.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /**
     * 返回32为大写md5
     *
     * @param st
     * @return
     */
    public static String getMD5(String st) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(st.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();

            for (byte item : array) {

                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));

            }
            return sb.toString().toUpperCase();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回16位以及32位的md5
     *
     * @param st
     * @return
     */
    public static String[] getMoreMD5(String st) {
        try {
            String result;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(st.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();

            return new String[]{
                    //16小写，16大写,32小写，32大写
                    buf.toString().substring(8, 24), buf.toString().substring(8, 24).toUpperCase(), result, result.toUpperCase()

            };
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件md5值
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());

    }

    /**
     * byte数组转16进制
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
