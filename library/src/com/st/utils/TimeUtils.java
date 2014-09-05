package com.st.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    private static final SimpleDateFormat dateFormat_MM_dd_HH_mm = new SimpleDateFormat("M-d HH:mm");

    public static String getCurrentDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    public static String getShortDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(dateString);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static String toDataString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(date);
    }

    public static String toDataStringByYYYYMMdd(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static String toDataStringByMMdd_HHmm(Date date) {
        return dateFormat_MM_dd_HH_mm.format(date);
    }

    public static String toChineseDateString(Date date) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        return cd.get(Calendar.YEAR) + "年" + (cd.get(Calendar.MONTH) + 1) + "月"
                + cd.get(Calendar.DATE) + "日";

    }

    public static Date toDateByString(String dateStr) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 两个时间段是否在5分钟以内
     * 
     * @param from
     * @param to
     * @return
     */
    public static boolean isBetweenFiveMinites(Date from, Date to) {

        if (from != null && to != null) {
            long minite = (to.getTime() - from.getTime()) / (60 * 1000);
            if (minite > 5) {
                return false;
            }
        }
        return true;
    }

    public static String formatDateTime(long baseTime) {
        long now = System.currentTimeMillis();
        long dif = now - baseTime;
        long dif_minute = dif / (60 * 1000);
        long dif_second = dif / (1000);
        if (dif <= 0) {
            return "刚刚";
        } else if (dif_second < 60 && dif_second > 0) {
            return dif_second + "秒前";
        } else if (dif_minute < 30 && dif_minute >= 1) {
            return dif_minute + "分钟前";
        } else {
            DateFormat dateFormat = new SimpleDateFormat("MM月dd日 hh:mm");
            return dateFormat.format(new Date(baseTime));
        }
    }

    /**
     * 判断字符串是否为时间格式
     * 
     * @param dateTime
     * @return
     */
    public static Date getDateTime(String dateTime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 返回四位年份
     * 
     * @param date
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getYear(Date date) {
        if (date == null)
            return -1;
        return date.getYear() + 1900;
    }

    /**
     * 返回月数(0-11)
     * 
     * @param date
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getMonth(Date date) {
        if (date == null)
            return -1;
        return date.getMonth();
    }

    /**
     * 返回月几(1-31)
     * 
     * @param date
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getMonthDay(Date date) {
        if (date == null)
            return -1;
        return date.getDate();
    }

    /**
     * 返回周几 returned value (<tt>0</tt> = Sunday, <tt>1</tt> = Monday, <tt>2</tt>
     * = Tuesday, <tt>3</tt> = Wednesday, <tt>4</tt> = Thursday, <tt>5</tt> =
     * Friday, <tt>6</tt> = Saturday)
     * 
     * @param date
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getWeekDay(Date date) {
        if (date == null)
            return -1;
        return date.getDay();
    }

}
