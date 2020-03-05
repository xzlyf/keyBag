package com.xz.utils.hardware;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.util.Locale;

/**
 * 系统参数
 */
public class SystemInfoUtil {

    /**
     * 常用信息合集
     *  示例：Genymotion_Custom_Android_7.1.1_000000000000000
     * @return
     */
    public static String getInfo(Context context) {

        return getSystemMake()
                + "_" + getSystemModel()
                + "_" + getDeviceBrand()
                + "_" + getSystemVersion()
                + "_" + getIMEI(context);

    }


    /**
     * 获取系统版本号
     * 实例：25
     *
     * @return
     */
    public static int getSystemVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 硬件制造商
     * 示例：Genymotion
     *
     * @return
     */
    public static String getSystemMake() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    ;

    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }
}
