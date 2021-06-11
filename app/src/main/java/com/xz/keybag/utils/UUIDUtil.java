package com.xz.keybag.utils;

import java.util.UUID;

public class UUIDUtil {
    /**
     * 过滤-(横杠)的UUID
     *
     * @return
     */
    public static String getStrUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 带格式的UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
