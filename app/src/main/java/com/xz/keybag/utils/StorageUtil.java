package com.xz.keybag.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author czr
 * @date 2020/8/10
 * 存储卡工具类
 * 可以获取内部存储等目录位置
 * <p>
 * 扫盲：
 * 内部存储位置:/data/data/包名/
 * 私有外部存储位置：/storage/emulated/0/Android/data/包名
 * 公开目录位置：/storage/emulated/0/
 * 内置存储卡挂载点：/storage/emulated/0/
 * 外置SD卡挂载点： /storage/sdcard1
 * 外置SD卡挂载点（某些设备）： /storage/mnt/
 * <p>
 * <p>
 * 1.使用内部存储是不需要权限的，内部存储属于应用的私有存储区域，其它应用不可访问，当应用被卸载时，内部存储中的文件也会被删除。
 * 2.外部存储分为公共目录和私有目录，外部存储是可以全局访问的，但需要申请权限
 * 3.Android4.4以后访问私有目录不需要申请权限
 */
public class StorageUtil {


    /**
     * 判断当前外部存储是否可读写
     *
     * @return
     */
    public static boolean canWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前外部存储是否可读
     *
     * @return
     */
    public boolean canReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 在内部存储创建一个文件夹
     * 最终显示：/data/data/<packer_name>/app_<name>/
     *
     * @param context
     */
    public static void createInternalDir(Context context, String name) {
        context.getDir(name, Context.MODE_PRIVATE);
    }

    /**
     * 返回内部存储的根目录
     * 最终显示：/data/data/包名
     * 部分机型显示：/data/user/0/包名
     * （这是安卓6.0之后的多用户设定，不同用户不能访问彼此数据，不影响操作。用户二则会显示：/data/user/1/包名，以此类推）
     *
     * @param context
     * @return
     */
    public static String getDataDir(Context context) {
        return context.getApplicationInfo().dataDir;
    }

    /**
     * 返回内部存储的缓存目录
     * 最终显示：/data/data/包名/cache
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * 返回内部存储的文件目录
     * 最终显示：/data/data/包名/files
     *
     * @param context
     * @return
     */
    public static File getFileDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 返回外部私有缓存目录
     * 最终显示：/storage/emulated/0/Android/data/包名/cache
     *
     * @param context
     * @return
     */
    public static File getExternalCache(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 返回外部私有文件目录
     * 最终显示：/storage/emulated/0/Android/data/包名/files
     *
     * @param context
     * @return
     */
    public static File getExternalFile(Context context) {
        return context.getExternalFilesDir("");
    }

    /**
     * 返回外部私有文件目录
     * 最终显示：/storage/emulated/0/Android/data/包名/files/<name>
     * 知道一个文件目录，没有则创建一个
     *
     * @param context
     * @return
     */
    public static File getExternalFile(Context context, String name) {
        return context.getExternalFilesDir(name);
    }


    /**
     * 返回内置存储卡分区和外置SD卡的私有目录地址。
     * file[0]:私有外部存储位置：/storage/emulated/0/Android/data/包名/files
     * file[1]:外置SD卡位置：/storage/0E05-3110/Android/data/包名/files
     *
     * @param context
     * @return
     */
    public static File[] getExternalFilesDir(Context context) {
        return context.getExternalFilesDirs("");
    }

    /**
     * 返回公开目录位置
     * 最终显示：/storage/emulated/0
     * @return
     */
    public static File getExternalDir(){
        return Environment.getExternalStorageDirectory();
    }
}
