package com.xz.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 复制到剪贴板管理器
 */
public class CopyUtil {

    private ClipboardManager cm;
    private ClipData mClicp;

    public CopyUtil(Context context) {
        cm = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);


    }

    /**
     * 复制到剪贴板
     * @param st 内容
     */
    public void copyToClicp(String st){

        mClicp = ClipData.newPlainText(System.currentTimeMillis()+"",st);
        cm.setPrimaryClip(mClicp);

    }

    /**
     * 获取剪贴板第一条内容
     * @return
     */
    public String getClicp(){

        return cm.getPrimaryClip().getItemAt(0).getText().toString();
    }

    /**
     * 获取剪贴板第n条内容
     * @param index
     * @return
     */
    public String getClicp(int index){

        return cm.getPrimaryClip().getItemAt(index).getText().toString();
    }


}
