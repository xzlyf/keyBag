package com.xz.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class TimeUtil {


    /**
     * 秒级
     * 时间戳→自定义格式时间
     *
     * @return
     */
    public static String getSimDate(String pattern, Long time) {
        return new SimpleDateFormat(pattern).format(new Date(Long.parseLong(time+"000")));//秒级转毫秒级
//        return new SimpleDateFormat(pattern).format(new Date(time));//毫秒级
    }
    /**
     * 毫秒级
     * 时间戳→自定义格式时间
     *
     * @return
     */
    public static String getSimMilliDate(String pattern, Long time) {
//        return new SimpleDateFormat(pattern).format(new Date(Long.parseLong(time+"000")));//秒级转毫秒级
        return new SimpleDateFormat(pattern).format(new Date(time));//毫秒级
    }

    /**
     * 日期 转 时间戳
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取startdate和enddate
     * <p>
     * [0] startdate
     * [1] enddate
     *
     * @param s
     * @return
     */
    public static String[] getStartAndEndDate(long s) {
        String startdate = getSimDate("yyyy-MM-dd", s) + " 00:00:00";
        String enddate = getSimDate("yyy-MM-dd", s) + " 23:59:59";

        startdate = getStringToDate(startdate, "yyyy-MM-dd HH:mm:ss") + "";
        enddate = getStringToDate(enddate, "yyyy-MM-dd HH:mm:ss") + "";
        return new String[]{startdate, enddate};
    }

    /**
     * 获取startdate 和enddate
     * 2.0
     *  [0]天start
     *  [1]天end
     *  [2]月start
     *  [3]月end
     */
    public static long[] getStartAndEndDateV2(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int maxday = TimeUtil.getMaxDayByMonth(time);
        int minday = TimeUtil.getMinDayByMonth(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long day_startdate = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long day_enddate = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, minday);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long month_startdate = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, maxday);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long month_enddate = calendar.getTimeInMillis();

        return new long[]{day_startdate, day_enddate, month_startdate, month_enddate};
    }

    /**
     * 获取一个月最大的天数
     */
    public static int getMaxDayByMonth(long time) {
        Calendar cal = Calendar.getInstance();
//        cal.set(year, month - 1, 1);
        cal.setTimeInMillis(time);
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取一个月最大的天数
     */
    public static int getMinDayByMonth(long time) {
        Calendar cal = Calendar.getInstance();
//        cal.set(year, month - 1, 1);
        cal.setTimeInMillis(time);
        return cal.getActualMinimum(Calendar.DATE);
    }
}
