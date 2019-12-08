package com.xz.utils;

import java.util.TreeMap;

public class SplicString {
    /**
     * 拼接URL
     * @param baseUrl
     * @param paraMap
     * @return
     */
    public static String SplicUrl(String baseUrl, TreeMap<String,String> paraMap){

        baseUrl = baseUrl+"?";
        //拼接Url
        for (String key :paraMap.keySet()){
            baseUrl  = baseUrl+key+"="+paraMap.get(key)+"&";
        }
        baseUrl = baseUrl.substring(0,baseUrl.length()-1);
        return baseUrl;
    }

    /**
     * 切割指定字符
     * @param text
     * @param c 要切割的字符
     * @return
     */
    public static String SplicString(String text,char c){
        String str = "";
        for (int i = 0;i<text.length();i++){
            if (text.charAt(i)!=c){
                str+=text.charAt(i);
            }
        }
        return str;
    }
}
