package ru.codingworkshop.gymm.ui.info.statistics;

import android.view.View;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.YEAR;

/**
 * Created by Radik on 12.12.2017.
 */

public class DateAxisFormatter implements IAxisValueFormatter {
    private final Calendar currentDate = Calendar.getInstance();
    private final Calendar formattedDate = Calendar.getInstance();
    private final DateFormat currentYearFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
    private final DateFormat currentCenturyFormat = new SimpleDateFormat("d MMM ''yy", Locale.getDefault());

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Date date = new Date((long) value);
        formattedDate.setTime(date);
        if (formattedDate.get(YEAR) == currentDate.get(YEAR)) {
            return currentYearFormat.format(date);
        } else {
            return currentCenturyFormat.format(date);
        }
    }
}
