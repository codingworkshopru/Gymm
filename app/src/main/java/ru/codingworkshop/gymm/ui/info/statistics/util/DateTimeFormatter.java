package ru.codingworkshop.gymm.ui.info.statistics.util;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class DateTimeFormatter {
    private DateTimeFormatter() {}

    public static String getDayAndMonth(Date date) {
        return new SimpleDateFormat("d MMM", Locale.getDefault()).format(date);
    }

    public static String getDayMonthAndYear(Date date) {
        return new SimpleDateFormat("d MMM ''yy", Locale.getDefault()).format(date);
    }

    public static String formatDateAndTime(@Nullable Date date) {
        if (date == null) {
            return "";
        }
        return DateFormat
                .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(date);
    }

    public static String formatDuration(Date start, Date finish) {
        if (start == null || finish == null) {
            return "";
        }
        long totalSeconds = (finish.getTime() - start.getTime()) / 1000;
        return new Formatter().format("%02d:%02d:%02d", totalSeconds / 3600, totalSeconds / 60 % 60, totalSeconds % 60).toString();
    }
}
