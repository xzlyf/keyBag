package com.xz.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 获取app版本号，版本名，App的名称工具类
 *
 */
public class PackageUtil {

    /**
     * 获取版本名
     * @param context
     * @return
     */
    public static String getVersionName(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(),0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(),0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取app名
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //获取应用 信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取albelRes
            int labelRes = applicationInfo.labelRes;
            //返回App的名称
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
