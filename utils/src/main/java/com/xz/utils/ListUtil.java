package com.xz.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    /**
     * 将一个集合拆分成制定长度的若干个集合
     *
     * @param list
     * @param len
     * @return
     */
    public static List<List<?>> splitList(List<?> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }

        List<List<?>> result = new ArrayList<List<?>>();


        int size = list.size();
        int count = (size + len - 1) / len;


        for (int i = 0; i < count; i++) {
            List<?> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

}
