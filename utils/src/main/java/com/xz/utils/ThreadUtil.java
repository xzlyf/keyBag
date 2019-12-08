package com.xz.utils;

import android.os.Handler;

/**
 * 线程工具类
 */
public class ThreadUtil {
    /**
     * 子线程运行
     * @param task 任务
     */
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }

    private static Handler handler = new Handler();
    /**
     * 主线程运行
     * @param task
     */
    public static void runInUIThread(Runnable task){
        handler.post(task);
    }
}
