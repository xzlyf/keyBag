package com.xz.utils;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * 流关闭工具类
 */
public class IOStreamUtil {
    public static void close(Closeable... list) {
        try {

            for (Closeable closeable : list) {
                if (closeable != null) {
                    closeable.close();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.w("xzlyf666", "IOSteamUril_ERROR: "+e.getMessage() );
        }
    }
}
