package com.xz.base;

import android.Manifest;

public class Local {

    public static int flag = -1;
    //待申请权限列表
    public static final String[] permission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
}
