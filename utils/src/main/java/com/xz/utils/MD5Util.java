package com.xz.utils;

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
}
