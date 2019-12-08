package com.xz.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xz.xzwidget.R;

public class IpEditView extends LinearLayout implements FinishEditListener {
    private Context mContext;
    private View view;
    private AutoFocusEditView ip1;
    private AutoFocusEditView ip2;
    private AutoFocusEditView ip3;
    private AutoFocusEditView ip4;

    public IpEditView(Context context) {
        super(context);
        init(context);
    }

    public IpEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_ip_editview, this, true);
        ip1 = view.findViewById(R.id.ip_1);
        ip2 = view.findViewById(R.id.ip_2);
        ip3 = view.findViewById(R.id.ip_3);
        ip4 = view.findViewById(R.id.ip_4);

        ip1.setLength(2);//当输入到第三位时自动跳转
        ip2.setLength(2);
        ip3.setLength(2);
        ip4.setLength(2);
        ip1.setNextEditView(ip2);
        ip2.setNextEditView(ip3);
        ip3.setNextEditView(ip4);
        ip2.setLastEditView(ip1);
        ip3.setLastEditView(ip2);
        ip4.setLastEditView(ip3);
        ip1.setFinishEditListener(this);
        ip2.setFinishEditListener(this);
        ip3.setFinishEditListener(this);
        ip4.setFinishEditListener(this);

    }



    /**
     * 重写可自行修改
     * @param ed
     * @param st
     */
    @Override
    public void onFinishText(EditText ed,String st) {
        //判断是否符合ip格式
        if (!st.equals("")) {
            int ip = Integer.valueOf(st);
            if (!(ip >= 0 && ip <= 255)) {
                Toast.makeText(mContext, "ip格式错误", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * 返回标准格式的ip地址
     *
     * @return
     */
    public String getIp() {
        return (ip1.getText().toString() + "." + ip2.getText().toString() + "." + ip3.getText().toString() + "." + ip4.getText().toString());
    }


}
