package com.xz.widget.toast;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.xzwidget.R;

public class XToast extends Toast {
    private static Toast resToast;
    public XToast(Context context) {
        super(context);

    }

    /**
     * @param context
     * @param tex
     * @param type    可自定义TYPE  也可使用自带的tpye  XType.XTOAST_TYPE_TIPS
     */
    public static void showToast(Context context, String tex, int type) {

        //获取系统的LayoutInflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast_x, null);

        TextView content = view.findViewById(R.id.tv_content);
        ImageView imgType = view.findViewById(R.id.tv_type);

        imgType.setImageResource(type);
        content.setText(tex);
        if (resToast == null) {

            resToast = new Toast(context);
            //resToast.setGravity(Gravity.CENTER, 0, 0);
            resToast.setGravity(Gravity.BOTTOM, 0, 80);
            resToast.setDuration(Toast.LENGTH_SHORT);
        }
        resToast.setView(view);
        resToast.show();


    }


}
