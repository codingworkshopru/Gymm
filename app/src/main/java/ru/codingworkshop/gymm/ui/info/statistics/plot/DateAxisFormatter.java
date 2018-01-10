package ru.codingworkshop.gymm.ui.info.statistics.plot;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;
import java.util.Date;

import ru.codingworkshop.gymm.ui.info.statistics.util.DateTimeFormatter;

import static java.util.Calendar.YEAR;

/**
 * Created by Radik on 12.12.2017.
 */

public class DateAxisFormatter implements IAxisValueFormatter {
    private final Calendar currentDate = Calendar.getInstance();
    private final Calendar formattedDate = Calendar.getInstance();

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Date date = new Date((long) value);
        formattedDate.setTime(date);
        if (formattedDate.get(YEAR) == currentDate.get(YEAR)) {
            return DateTimeFormatter.getDayAndMonth(date);
        } else {
            return DateTimeFormatter.getDayMonthAndYear(date);
        }
    }
}
