package cn.lanyue.cas.utils;


import cn.lanyue.cas.common.Constant;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Stream;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils{

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyyMM"};

    public static final String AM = "AM";

    public static final String PM = "PM";


    public static LocalDate date2LocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date parseDate(Object str) {
        if (str == null){
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 上下午业务中的定义
     * 上午：0---12
     * 下午：12---24
     * @return 上午还是下午
     */
    public static String checkTimeInterval() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        if (0 <= hour && hour <= 12) {
            return AM;
        }
        return PM;
    }

    public static LocalDate monthStart(LocalDate localDate) {
        return LocalDate.of(localDate.getYear(),localDate.getMonth(),1);
    }

    public static LocalDate monthEnd(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate weekStart(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.previous(java.time.DayOfWeek.SUNDAY)).plusDays(1);
    }

    public static LocalDate weekEnd(LocalDate localDate) {
        return weekStart(localDate).plusDays(6);
    }


    public static List<String> betweenDays(Date startDate, Date endDate) {
        if (isSameDay(startDate, endDate)) {
            return Arrays.asList(formatDate(startDate));
        }

        List<String> days = Lists.newArrayList();
        days.add(formatDate(startDate));
        while (startDate.before(endDate)) {
            startDate = addDays(startDate,1);
            days.add(formatDate(startDate));
        }
        return days;
    }

    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    public static List<LocalDate> betweenDays(String date) {
        LocalDate now = LocalDate.now();
        LocalDate start ;
        LocalDate end ;
        if (Constant.MONTH.equals(date)) {
            start = monthStart(now);
            end = monthEnd(now);
            return betweenDays(start, end);
        }
        if (Constant.WEEK.equals(date)) {
            start = weekStart(now);
            end = weekEnd(now);
            return betweenDays(start, end);
        }
        if (Constant.LAST_MONTH.equals(date)) {
            now = now.plusMonths(-1);
            start = monthStart(now);
            end = monthEnd(now);
            return betweenDays(start, end);
        }

        if (Constant.TODAY.equals(date)) {
            return betweenDays(now, now);
        }

        return Collections.emptyList();
    }



    public static List<LocalDate> betweenDays(LocalDate startDate, LocalDate endDate) {
        Preconditions.checkArgument(startDate.compareTo(endDate) <= 0, "start cannot be before end");

        LocalDate now = LocalDate.now();

        List<LocalDate> list = new ArrayList<>();

        if (ObjectUtils.equals(startDate, endDate)) {
            list.add(startDate);
            return list;
        }

        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return list;
        }
        Stream.iterate(startDate, d -> d.plusDays(1) ).limit(distance + 1).filter(f->f.isBefore(now)).forEach(f ->list.add(f));
        list.add(now);
        return list;
    }

    public static void main(String[] args) throws Exception{
        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = "2020-01-01";
        String date2 = "2020-01-06";

        Date parse = simpleDateFormat.parse(date1);
        Date parse2 = simpleDateFormat.parse(date2);*/

        LocalDate now1= LocalDate.of(2020,02,27);
        LocalDate now2 = LocalDate.of(2020,02,27);

        betweenDays(now1, now2).forEach(System.out::println);
    }

}
