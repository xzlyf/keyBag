package com.xz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Size;
import android.view.Display;
import android.view.Window;

import androidx.annotation.RequiresApi;

public class ViewUtil {
    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 获取屏幕宽高
     *
     * @param activity
     * @return Size().getWidth() 宽  Size().getHeight()  高
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return new Size(display.getWidth(), display.getHeight());
    }

    /**
     * 获取屏幕宽高
     *
     * @param activity
     * @return [宽，高]
     */
    public static int[] getScreenSizeV2(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return new int[]{display.getWidth(), display.getHeight()};
    }


}
