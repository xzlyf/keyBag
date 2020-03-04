package com.xz.utils.network;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;

public class Ping {


    /**
     * 注意ping命令没有约束请求次数，会无限请求下去的
     * @param ip
     */
    public static void ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = null;
        try {
            ipProcess = runtime.exec("ping " + ip);
            InputStream is = ipProcess.getInputStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                Logger.w("接收到：" + new String(buf, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //在结束的时候应该对资源进行回收
            if (ipProcess != null) {
                ipProcess.destroy();
            }
            runtime.gc();
        }
    }
}
