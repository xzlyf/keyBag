package com.xz.base;

import android.app.Activity;
import android.app.Application;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class BaseApplication extends Application {
    private List<Activity> activities = new ArrayList<>();
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLog();
    }



    private void initLog() {
        Logger.init("xzlyf")    //LOG TAG默认是PRETTYLOGGER
                .methodCount(2)                 // 决定打印多少行（每一行代表一个方法）默认：2
                .methodOffset(2);             // 默认：0
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    /**
     * 新建了一个activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 应用退出，结束所有的activity
     */
    public void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
    }
}
