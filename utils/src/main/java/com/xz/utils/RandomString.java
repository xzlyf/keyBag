package com.xz.utils;

import java.util.Random;

/**
 * 生成随机字符串
 */
public class RandomString {
    /**
     * 返回指定长度的随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length){
        String st = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234657980";
        Random random = new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(36);
            sb.append(st.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 返回指定长度的随机字符串，可指定大小写
     * @param length
     * @param isLower
     * @return
     */
    public static String getRandomString(int length,boolean isLower){
        String st = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234657980abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(st.length());
            sb.append(st.charAt(number));
        }
        if (isLower){
            return sb.toString().toLowerCase();
        }else{
            return sb.toString();
        }
    }
}
