package com.xz.base.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/4/15.
 */
public class ToastUtil {
    private static Toast toast;
    public static void Shows(Context mthis, String Message){
        if (toast == null){
            toast = Toast.makeText(mthis,Message, Toast.LENGTH_SHORT);
        }else {
            toast.setText(Message);
        }
        toast.show();
    }

    public static void Shows_LONG(Context mthis, String Message){
        if (toast == null){
            toast = Toast.makeText(mthis,Message, Toast.LENGTH_LONG);
        }else {
            toast.setText(Message);
        }
        toast.show();
    }
}
