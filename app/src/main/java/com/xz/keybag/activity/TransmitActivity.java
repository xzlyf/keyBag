package com.xz.keybag.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;

import java.lang.reflect.Method;

public class TransmitActivity extends BaseActivity {


    @Override
    public boolean homeAsUpEnabled() {
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_transmit;
    }

    @Override
    public void initData() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称
            apConfig.SSID = "我是热点";
            //配置热点的密码(至少8位)
            apConfig.preSharedKey = "12345678";
            apConfig.allowedKeyManagement.set(4);
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            Boolean rs = (Boolean) method.invoke(wifiManager, apConfig, true);//true开启热点 false关闭热点
            Log.d("-------", "开启是否成功:" + rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
