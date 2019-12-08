package com.xz.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * 检查应用是否存在于手机
 * 检查包名是否存在于手机
 */
public class CheckPackage {
    /**
     *
     * @param context
     * @param packeage
     * @return true 存在 false 不存在
     */
    public static boolean checkPackage(Context context, String packeage) {
        PackageManager manager = context.getPackageManager();
        //获取已安装的应用包信息
        List<PackageInfo> infos = manager.getInstalledPackages(0);
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).packageName.equalsIgnoreCase(packeage)){
                return true;
            }
        }
        return false;
    }
}
