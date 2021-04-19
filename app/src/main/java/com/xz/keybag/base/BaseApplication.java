package com.xz.keybag.base;

import android.app.Activity;
import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.xz.keybag.base.utils.PreferencesUtilV2;
import com.xz.keybag.jni.NativeUtils;

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
		//初始prefercences工具
		PreferencesUtilV2.initPreferencesUtils(this, "keybag");
		NativeUtils.signatureVerify(this, true);
	}


	private void initLog() {

		FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
				.showThreadInfo(true)      //（可选）是否显示线程信息。 默认值为true
				.methodCount(1)               // （可选）要显示的方法行数。 默认2
				.methodOffset(0)               // （可选）设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
				.tag("xzlyf")                  //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
				.build();
		Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

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
