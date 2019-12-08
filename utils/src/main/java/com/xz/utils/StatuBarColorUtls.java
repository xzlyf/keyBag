package com.xz.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.Window;

/**
 * 设置状态栏颜色
 */
public class StatuBarColorUtls {
    private Window window;
    private int defultColor;//默认状态栏颜色

    public StatuBarColorUtls(Activity activity) {
        window = activity.getWindow();
        defultColor = window.getStatusBarColor();
    }

    public void setStatuBarColor(int color) {
        window.setStatusBarColor(color);
    }

    public void setStatuBarColor(String colorSt) {
        window.setStatusBarColor(Color.parseColor(colorSt));
    }
    public void setDefultColor(){
        window.setStatusBarColor(defultColor);

    }
}
