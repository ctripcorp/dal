package com.ctrip.platform.dal.daogen.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by taochen on 2019/7/26.
 */
public class DateUtils {
    // one hour delay
    public static long getFixInitDelay(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime()/1000 - checkTime.getTime()/1000;
    }

    //当前时间距00:00延时时间
    public static long getZeroInitDelay(Date nowDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime()/1000 - nowDate.getTime()/1000;
    }

    public static boolean checkIsSendEMailTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //每周一上午9点发邮件
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
                calendar.get(Calendar.HOUR_OF_DAY) == 9) {
            return true;
        }
        return false;
    }

    public static String formatCheckTime(Date convertCheckTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(convertCheckTime).replaceAll("-| ", "");
        return dateString.substring(0, dateString.indexOf(":"));
    }

    public static String getStartOneWeek(Date nextWeekDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextWeekDate);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return formatCheckTime(calendar.getTime());
    }

    public static String getEndOneWeek(Date nextWeekDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nextWeekDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return formatCheckTime(calendar.getTime());
    }

    public static String getBeforeOneDay(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return formatCheckTime(calendar.getTime());
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    public static String getBeforeOneHourDateString(Date checkTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkTime);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date catTransactionDate = calendar.getTime();
        return DateUtils.formatCheckTime(catTransactionDate);
    }
}
