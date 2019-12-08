package com.xz.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理工具类
 * 支持一键移除活动
 */
public class ActivityUtil {

    private static ActivityUtil instance;

    // 单例模式中获取唯一的ExitApplication实例
    public static synchronized ActivityUtil getInstance() {
        if (null == instance) {
            instance = new ActivityUtil();
        }
        return instance;
    }

    public static List<Activity> list = new ArrayList<>();

    public void addActivity(Activity activity){
        list.add(activity);
    }
    public void removeActivity(Activity activity){
        list.remove(activity);
    }
    public void finishAll(){
        for (Activity ac:list){
            if (!ac.isFinishing()){
                ac.finish();
            }
        }
        list.clear();
    }
}
