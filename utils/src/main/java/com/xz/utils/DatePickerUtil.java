package com.xz.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;

/**
 * 日期选择器
 */
public class DatePickerUtil {
    private static Calendar ca = Calendar.getInstance();
    private static int mYear = ca.get(Calendar.YEAR);
    private static int mMonth = ca.get(Calendar.MONTH);
    private static int mDay = ca.get(Calendar.DAY_OF_MONTH);
    private static int mHour = ca.get(Calendar.HOUR_OF_DAY);
    private static int mMin = ca.get(Calendar.MINUTE);
    private static int mSec = ca.get(Calendar.SECOND);

    /**
     * 日期选择器
     * @param context
     * @param listener
     */
    public static void showDatePicker(Context context, DatePickerDialog.OnDateSetListener listener) {
        new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, listener, mYear, mMonth, mDay).show();
    }
    public static void showTimePicker(Context context, TimePickerDialog.OnTimeSetListener listener){
        new TimePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT,listener,mHour,mMin,true).show();
    }


    public static int getYear() {
        return mYear;
    }

    public static int getMonth() {
        return mMonth;
    }

    public static int getDay() {
        return mDay;
    }

    public static void setmYear(int mYear) {
        DatePickerUtil.mYear = mYear;
    }

    public static void setmMonth(int mMonth) {
        DatePickerUtil.mMonth = mMonth;
    }

    public static void setmDay(int mDay) {
        DatePickerUtil.mDay = mDay;
    }
}
