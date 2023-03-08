package com.jy2b.zxxfd.utils;

import ch.qos.logback.core.util.TimeUtil;
import cn.hutool.core.date.DateUnit;
import com.jy2b.zxxfd.domain.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeUtils {

    /**
     * 将Date时间转为字符串
     * @param date 时间
     * @return String
     */
    public static String dateToStringTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(date);
    }

    /**
     * 获取现在时间
     * @return String
     */
    public static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        return LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
    }

    /**
     * 将字符串时间转为LocalDateTime
     * @param time 时间
     * @return LocalDateTime
     */
    public static LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        return LocalDateTime.parse(time, formatter);
    }


    /**
     * 获取东八区当前时间
     * @return LocalDateTime
     */
    public static LocalDateTime nowLocalDateTime() {
        return stringToLocalDateTime(now());
    }

    /**
     * 将LocalDateTime转为Date 默认时区为东8区
     * @param localDateTime localDateTime
     * @return Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }

    /**
     * 获取指定时间段中的全部日期
     * @param start 起始时间戳
     * @param end 结束时间戳
     * @return List<Date>
     */
    public static List<Date> getDumDateList(Long start, Long end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        // 一天的毫秒值
        long oneDay = 24 * 60 * 60 * 1000;
        // 设置当前时间
        long time = start;

        List<Date> dateList = new ArrayList<>();
        while (time <= end) {
            Date dateTime = new Date(time);
            String format = simpleDateFormat.format(dateTime);
            try {
                Date date = simpleDateFormat.parse(format);
                dateList.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            time += oneDay;
        }

        return dateList;
    }
}
