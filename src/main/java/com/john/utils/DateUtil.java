package com.john.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final long ZERO_TIMESTAMP = 1044028800000L;
    public static final long RELEASE_ZERO_TIMESTAMP = 1298908800000L;
    private static final DateFormat FMT_DATE = new SimpleDateFormat("MM-dd");
    private static final DateFormat FMT_DATE2 = new SimpleDateFormat("yy-MM-dd");
    private static final DateFormat FMT_TIME = new SimpleDateFormat("HH:mm");
    private static final DateFormat FMT_DATE_TIME = new SimpleDateFormat(
            "MM-dd HH:mm");
    private static final DateFormat FMT_DATE_TIME2 = new SimpleDateFormat(
            "yy-MM-dd HH:mm");
    private static long TIME_CALIBRATOR = 0;

    public static String format(Date d) {
        if (d == null || d.getTime() < ZERO_TIMESTAMP)
            return "";
        Date now = new Date();
        if (now.getYear() == d.getYear() && now.getMonth() == d.getMonth()
                && now.getDate() == d.getDate())
            return FMT_TIME.format(d);
        else if (now.getYear() == d.getYear())
            return FMT_DATE.format(d);
        else
            return FMT_DATE2.format(d);
    }

    public static String format2(Date d) {
        if (d == null || d.getTime() < ZERO_TIMESTAMP)
            return "";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long today = c.getTimeInMillis();
        long dl = d.getTime();
        if (dl > today + 24 * 3600 * 1000)
            return FMT_DATE2.format(d);
        if (dl > today)
            return FMT_TIME.format(d);
        if (dl > today - 24 * 3600 * 1000)
            return "昨天 " + FMT_TIME.format(d);
        if (dl > today - 48 * 3600 * 1000)
            return "前天 " + FMT_TIME.format(d);
        int toyear = c.get(Calendar.YEAR);
        c.setTimeInMillis(dl);
        int dyear = c.get(Calendar.YEAR);
        if (toyear == dyear)
            return FMT_DATE.format(d);
        else
            return FMT_DATE2.format(d);
    }

    public static String format2t(Date d) {
        if (d == null || d.getTime() < ZERO_TIMESTAMP)
            return "";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long today = c.getTimeInMillis();
        long dl = d.getTime();
        if (dl > today + 24 * 3600 * 1000)
            return FMT_DATE_TIME2.format(d);
        if (dl > today)
            return FMT_TIME.format(d);
        if (dl > today - 24 * 3600 * 1000)
            return "昨天 " + FMT_TIME.format(d);
        if (dl > today - 48 * 3600 * 1000)
            return "前天 " + FMT_TIME.format(d);
        int toyear = c.get(Calendar.YEAR);
        c.setTimeInMillis(dl);
        int dyear = c.get(Calendar.YEAR);
        if (toyear == dyear)
            return FMT_DATE_TIME.format(d);
        else
            return FMT_DATE_TIME2.format(d);
    }

    // RFC 1123 date format
    public static void setHttpResponseDate(String date) {
        try {
            DateFormat fmt = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

            Date d = fmt.parse(date.trim());

            if (d.getTime() < RELEASE_ZERO_TIMESTAMP)
                return;

            TIME_CALIBRATOR = d.getTime() - System.currentTimeMillis();

        } catch (Exception e) {
            // just ignore
        } catch (Error error) {
            // just ignore
        }
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis() + TIME_CALIBRATOR;
    }

    public static long getNextDayTimeMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 24);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long timeCalibrator() {
        return TIME_CALIBRATOR;
    }

    // today starts at 0:00:00AM in +8:00 zone
    public static long today(long now) {
        long l = now;
        l += 8 * 3600000;
        l = l - l % (24 * 3600000);
        l -= 8 * 3600000;
        return l;
    }
}
