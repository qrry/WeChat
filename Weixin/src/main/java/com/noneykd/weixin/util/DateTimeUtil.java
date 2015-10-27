package com.noneykd.weixin.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public final static String FULL_WITH_WEEK = "yyyy-MM-dd HH:mm:ss (E)";
    public final static String DATE_WITH_WEEK = "yyyy/MM/dd(E)";
    public final static String FULL_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public final static String FULL_FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    public final static String FULL_FORMAT_HM = "yyyy-MM-dd HH:mm";
    public final static String FULL_FORMAT2 = "yyyyMMddHHmmss";
    public final static String FULL_FORMAT_MILLION = "yyyyMMddHHmmssSSS";
    public final static String DATE_FORMAT = "yyyy/MM/dd";
    public final static String DATE_FORMATYEAR = "yyyy-MM-dd";
    public final static String DATE_WITH_HOUR = "yyyy-MM-dd-HH";
    public final static String TIME_FORMAT = "HH:mm:ss";
    public final static String SHORT_TIME_FORMAT = "HH:mm";
    public final static String FORMAT_ZONE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public final static String HOUR_FORMAT = "yyyy-MM-dd-HH";
    

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(getFullFormatWithWeek(date));
        System.out.println(getDateWithWeek(date));

        System.out.println(getFullFormatWithWeek(createDate(2014, 07, 20, 18,
                56, 48, 636)));

        System.out.println(parse("2014-07-20 18:56:48 (星期日)", FULL_WITH_WEEK));
        System.out.println(parse("2015-07-29T10:16:44.001Z", FORMAT_ZONE));

        System.out.println(parse("09:12", SHORT_TIME_FORMAT));
    }

    /**
     * date转换为类格式:2015-07-23 09:24:23 (星期四)
     * 
     * @param date
     * @return
     */
    public static String getFullFormatWithWeek(Date date) {
        DateTime dt = new DateTime(date);
        return dt.toString(FULL_WITH_WEEK);
    }

    /**
     * date转换为类格式：2015/07/23 (星期四)
     * 
     * @param date
     * @return
     */
    public static String getDateWithWeek(Date date) {
        DateTime dt = new DateTime(date);
        return dt.toString(DATE_WITH_WEEK);
    }

    public static Date createDate(int year, int month, int day, int hour,
            int min, int sed, int millSed) {
        DateTime dt = createDateTime(year, month, day, hour, min, sed, millSed);
        return dt.toDate();
    }

    public static DateTime createDateTime(int year, int month, int day,
            int hour, int min, int sed, int millSed) {
        DateTime dt = new DateTime(year, month, day, hour, min, sed, millSed);
        return dt;
    }

    public static String format(Date date, String pattern) {
        DateTime dt = new DateTime(date);
        return dt.toString(pattern);
    }

    public static Date parse(String dateTime, String pattern) {
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        return parse(dateTime, format);
    }

    public static Date parse(String dateTime, DateTimeFormatter format) {
        DateTime dt = DateTime.parse(dateTime, format);
        return dt.toDate();
    }

    public static Date cutYearMonthDay(Date date) {
        DateTime dt = new DateTime(date);
        return createDate(dt.getYear(), dt.getMonthOfYear(),
                dt.getDayOfMonth(), 0, 0, 0, 0);
    }

    public static DateTime cutYearMonthDay(DateTime dt) {
        return new DateTime(dt.getYear(), dt.getMonthOfYear(),
                dt.getDayOfMonth(), 0, 0, 0, 0);
    }

    public static Date cutHourMinSecondForDate(Date date) {
        DateTime dt = cutHourMinSecondForDateTime(date);
        return dt.toDate();
    }

    public static Date cutHourMinSecondForDate(DateTime dt) {
        return cutHourMinSecondForDateTime(dt).toDate();
    }

    public static DateTime cutHourMinSecondForDateTime(Date date) {
        DateTime dt = new DateTime(date);
        return createDateTime(1970, 1, 1, dt.getHourOfDay(),
                dt.getMinuteOfHour(), dt.getSecondOfMinute(),
                dt.getMillisOfSecond());
    }

    public static DateTime cutHourMinSecondForDateTime(DateTime dt) {
        return createDateTime(1970, 1, 1, dt.getHourOfDay(),
                dt.getMinuteOfHour(), dt.getSecondOfMinute(),
                dt.getMillisOfSecond());
    }

    /**
     * 根据日期范围返回范围内日期集合.
     * 
     * @param begin
     * @param end
     * @return
     */
    public static List<Date> listDay(final Date begin, final Date end) {
        if (begin == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }
        if (end == null) {
            throw new IllegalArgumentException("结束日期不能为空");
        }
        if (begin.after(end)) {
            throw new IllegalArgumentException("开始时间不能大于结束时间");
        }
        List<Date> containDays = new ArrayList<Date>();
        for (Date temp = (Date) begin.clone(); !DateUtils.isSameDay(temp, end); temp = DateUtils
                .addDays(temp, 1)) {
            containDays.add(temp);
        }
        containDays.add(end);
        return containDays;
    }

    /**
     * 根据日期范围返回范围内月份集合.
     * 
     * @param begin
     * @param end
     * @return
     */
    public static List<Date> listMonth(final Date begin, final Date end) {
        if (begin == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }
        if (end == null) {
            throw new IllegalArgumentException("结束日期不能为空");
        }
        if (begin.after(end)) {
            throw new IllegalArgumentException("开始时间不能大于结束时间");
        }
        Set<Date> containMonths = new HashSet<Date>();
        for (Date temp = (Date) begin.clone(); !DateUtils.isSameDay(temp, end); temp = DateUtils
                .addDays(temp, 1)) {
            Date tempClone = (Date) temp.clone();
            tempClone = DateUtils.setDays(tempClone, 1);
            containMonths.add(tempClone);
        }
        Date endClone = (Date) end.clone();
        endClone = DateUtils.setDays(endClone, 1);
        containMonths.add(endClone);
        List<Date> resultList = new ArrayList<Date>();
        for (Iterator<Date> iterator = containMonths.iterator(); iterator
                .hasNext();) {
            resultList.add(iterator.next());
        }
        return resultList;
    }

}
