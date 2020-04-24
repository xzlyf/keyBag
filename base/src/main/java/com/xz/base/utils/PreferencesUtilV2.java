package com.xz.base.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * SP工具类2.0
 */
public class PreferencesUtilV2 {

    /*
    apply() 异步提交
    commit() 同步提交，效率会比apply异步提交的速度慢，但是apply没有返回值，永远无法知道存储是否失败

     */
    private static Application instance;
    private static SharedPreferences sp;

    public static void initPreferencesUtils(Application application, String fileName) {
        instance = application;
        sp = instance.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
    }

    public static void putString(String key, String data) {
        sp.edit().putString(key, data).apply();
    }

    public static String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public static void putInt(String key, int data) {
        sp.edit().putInt(key, data).apply();
    }

    public static int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public static void putLong(String key, long data) {
        sp.edit().putLong(key, data).apply();
    }

    public static long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    public static void putBoolean(String key, boolean data) {
        sp.edit().putBoolean(key, data).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public static void putFloat(String key, float data) {
        sp.edit().putFloat(key, data).apply();
    }

    public static float getFloat(String key, float defalut) {
        return sp.getFloat(key, defalut);
    }

    public static void putStringSet(String key, Set<String> data) {
        sp.edit().putStringSet(key, data).apply();
    }

    public static Set<String> getStringSet(String key, Set<String> defalut) {
        return sp.getStringSet(key, defalut);
    }

    public static Map<String, ?> getAll() {
        return sp.getAll();
    }

}
