package com.xz.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 系统相关工具包
 */
public class SystemUtil {

    /**
     * 兼容 安卓7 安装器
     *
     * 需要在AndroidManifest清单文件中加入以下内容
     * <p>
     *      <provider
     *             android:name="androidx.core.content.FileProvider"
     *             android:authorities="${applicationId}.fileprovider"
     *             android:exported="false"
     *             android:grantUriPermissions="true">
     *             <meta-data
     *                 android:name="android.support.FILE_PROVIDER_PATHS"
     *                 android:resource="@xml/file_paths" />
     *         </provider>
     * </p>
     *
     * ***xml/file_paths文件内容
     * <p>
     *     <paths xmlns:android="http://schemas.android.com/apk/res/android">
     *     <external-path
     *         name="files_root"
     *         path="Android/data/${applicationId}/files/update" />-->
     *     <external-path
     *         name="external"
     *         path="." />
     *
     * </paths>
     * </p>
     */
    @RequiresPermission(android.Manifest.permission.REQUEST_INSTALL_PACKAGES)
    public static void newInstallAppIntent(Context context, String apkFile) {

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            intent.setDataAndType(Uri.fromFile(new File(apkFile)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            // 声明需要的临时的权限
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // 第二个参数，即第一步中配置的authorities
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(apkFile));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }
}
