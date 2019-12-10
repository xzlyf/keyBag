package com.xz.keybag.sql;

import com.orhanobut.logger.Logger;
import com.xz.utils.DESUtil;

public class EOD {


    /**
     * 解密
     */
    public static String decrypt(String HEX, String key) {
        try {
            return DESUtil.decrypt(HEX, key);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.w(e.getMessage());
            return null;
        }
    }

    /**
     * 加密
     */
    public static String encrypt(String st, String key) {

        return DESUtil.encrypt(st, key);

    }

}
