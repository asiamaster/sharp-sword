package com.dili.ss.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by asiam on 2017/4/28 0028.
 */
public class DateUtils {

    public static long getServerTime() {
        return System.currentTimeMillis();
    }

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(String format) {
        return format(new Date(), format);
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        } else {
            try {
                SimpleDateFormat e = new SimpleDateFormat(format);
                return e.format(date);
            } catch (Exception var3) {
                throw new RuntimeException("日期格式化转换失败", var3);
            }
        }
    }

    public static String dateFormat(long time) {
        return format(new Date(time), "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(String dateStr, String oldFromat, String newFormat) {
        try {
            if (StringUtils.isBlank(dateStr)) {
                return null;
            } else {
                SimpleDateFormat e = new SimpleDateFormat(oldFromat);
                Date date = e.parse(dateStr);
                return format(date, newFormat);
            }
        } catch (Exception var5) {
            throw new RuntimeException("日期格式化转换失败", var5);
        }
    }

    public static Calendar format(String dateStr, String dateStrFormat) {
        try {
            if (StringUtils.isBlank(dateStr)) {
                return null;
            } else {
                SimpleDateFormat e = new SimpleDateFormat(dateStrFormat);
                Date date = e.parse(dateStr);
                Calendar ca = GregorianCalendar.getInstance();
                ca.setTime(date);
                return ca;
            }
        } catch (Exception var5) {
            throw new RuntimeException("日期格式化转换失败", var5);
        }
    }

    public static Date dateStr2Date(String dateStr, String dateStrFormat) {
        try {
            if (StringUtils.isBlank(dateStr)) {
                return null;
            } else {
                SimpleDateFormat e = new SimpleDateFormat(dateStrFormat);
                return e.parse(dateStr);
            }
        } catch (Exception var3) {
            throw new RuntimeException("日期格式化转换失败", var3);
        }
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date reduceHours(Date date, int amount) {
        if (null == date) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (amount < 0) {
                cal.add(11, amount);
            } else {
                cal.add(11, -amount);
            }

            return cal.getTime();
        }
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliSeconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    public static Date addSeconds(int amount) {
        return add(new Date(), 13, amount);
    }

    private static Date add(Date date, int field, int amount) {
        if (null == date) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(field, amount);
            return cal.getTime();
        }
    }

    public static String formatDate2DateTimeStart(String dateStr) {
        Calendar calendar = format(dateStr, "yyyy-MM-dd");
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        return format(calendar.getTime());
    }

    public static String formatDate2DateTimeEnd(String dateStr) {
        Calendar calendar = format(dateStr, "yyyy-MM-dd");
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        return format(calendar.getTime());
    }

    public static Date formatDate2DateTimeStart(Date date) {
        if (date == null) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(11, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            return calendar.getTime();
        }
    }

    public static Date formatDate2DateTimeEnd(Date date) {
        if (date == null) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            return calendar.getTime();
        }
    }

    public static Date formatDateStr2Date(String date) {
        return dateStr2Date(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Date addMinutes(Date nextDate, int amount) {
        return add(new Date(), 12, amount);
    }

    public static boolean equals(Date time1, Date time2) {
        return (time1 != null || time2 == null) && (time1 == null || time2 != null) ? (time1 == null && time2 == null ? true : StringUtils.equals(format(time1), format(time2))) : false;
    }

    /**
     * date2比date1多的天数
     * 只是通过日期来进行比较两个日期的相差天数的比较，没有精确到相差到一天的时间。如果是只是纯粹通过日期（年月日）来比较比较的话就是方式一。
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);

        //如果date1比date2靠后，交换年和日
        if(date1.after(date2)){
            int tmp = year1;
            year1 = year2;
            year2 = tmp;
            tmp = day1;
            day1 = day2;
            day2 = tmp;
        }
        //不同年
        if (year1 != year2){
            int timeDistance = 0;

            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        }//同一年
        else {
            return day2 - day1;
        }
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * 是通过计算两个日期相差的毫秒数来计算两个日期的天数差的。一样会有一个小问题，就是当他们相差是23个小时的时候，它就不算一天了。如下面的两个日期
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        return (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
    }
}
