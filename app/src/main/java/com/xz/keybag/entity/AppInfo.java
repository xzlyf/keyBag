package com.xz.keybag.entity;

import android.graphics.drawable.Drawable;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class AppInfo {

	private String packageName;
	private String appName;
	private Drawable icon;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
