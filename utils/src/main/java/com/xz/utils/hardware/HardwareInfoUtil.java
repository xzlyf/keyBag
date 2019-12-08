package com.xz.utils.hardware;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;

public class HardwareInfoUtil {

    private TelephonyManager phone;
    private WifiManager wifiManager;
    private Display display;
    private DisplayMetrics metrics;

    public HardwareInfoUtil(Activity activity) {
        phone = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        display = activity.getWindowManager().getDefaultDisplay();
        metrics = activity.getResources().getDisplayMetrics();

    }

}
