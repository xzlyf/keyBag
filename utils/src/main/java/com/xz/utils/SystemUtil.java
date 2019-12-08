package com.xz.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 系统相关工具包
 */
public class SystemUtil {

    /**
     * 兼容 安卓8 安装器
     * @param context
     * @param file
     * @return
     */
    public static Intent newInstallAppIntent(Context context,File file){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        if (Build.VERSION.SDK_INT >=24){
            Uri apkUri = FileProvider.getUriForFile(context,"com.xz.ska",file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,type);
        }else{
            intent.setDataAndType(Uri.fromFile(file),type);
        }


        return intent;

    }
}
