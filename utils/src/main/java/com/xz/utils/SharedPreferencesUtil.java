package com.xz.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SP工具类
 */
public class SharedPreferencesUtil {

    /*
    apply() 异步提交
    commit() 同步提交，效率会比apply异步提交的速度慢，但是apply没有返回值，永远无法知道存储是否失败

     */


    /**
     * =============================================================================================
     * @param context
     * @param fileName
     * @param key
     * @param data
     */
    public static void saveString(Context context, String fileName,String key,String data){
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, data).apply();
    }

    public static String getString(Context context,String fileName, String key, String defValue){
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }
    /**
     * =============================================================================================
     * @param context
     * @param fileName
     * @param key
     * @param data
     */
    public static void saveInt(Context context, String fileName,String key,int data){
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, data).apply();
    }

    public static int getInt(Context context, String fileName, String key, int defValue){
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getInt(key,defValue);
    }
    /**
     * =============================================================================================
     * @param context
     * @param fileName
     * @param key
     * @param data
     */
    public static void saveBoolean(Context context, String fileName,String key,boolean data){
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, data).apply();
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defValue){
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }



}
