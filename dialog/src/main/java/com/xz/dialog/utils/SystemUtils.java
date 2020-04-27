package com.xz.dialog.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author czr
 * @date 2020/4/10
 */
public class SystemUtils {
    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取当前屏幕状态
     *
     * @param context
     * @return Configuration.ORIENTATION_LANDSCAPE 横向  Configuration.ORIENTATION_PORTRAIT 竖屏
     */
    public static int scrennMode(Context context) {
        Configuration configuration = context.getResources().getConfiguration(); //获取设置的配置信息
        return configuration.orientation;

    }


}
