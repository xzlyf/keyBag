package com.xz.keybag.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;

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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 将px值转换为sp值
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px2(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
