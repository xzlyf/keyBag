package com.xz.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘工具类
 */
public class KeyBoardUtil {

    /**
     * 弹出键盘
     *
     * @param view
     */
    public static void showKeyBoard(View view) {
        InputMethodManager methodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (methodManager != null) {
            view.requestFocus();
            methodManager.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param view
     */
    public static void hideKeyBoard(View view) {
        InputMethodManager methodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (methodManager != null) {
            view.clearFocus();
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
