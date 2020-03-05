package com.xz.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtil {

    /**
     * 指定长度切割数组
     * 示例： [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 ]
     * 指定分割长度:6
     * 结果:      [1, 2, 3, 4, 5, 6]
     * [7, 8, 9, 10, 11, 12]
     * [13, 14]
     *
     * @param data
     * @return
     */
    public static List<byte[]> optByte(int div, byte[] data) {

        int length = data.length;
        int mod = length % div;// 余数
        int total = length / div;// 商
        int num = 0;
        int cla = 0;

        List<byte[]> base = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            base.add(Arrays.copyOfRange(data, num, cla + div));
            cla = cla + div;
            num = cla;
        }
        if (mod > 0) {
            base.add(Arrays.copyOfRange(data, cla, cla + mod));
        }

        return base;
    }
}
