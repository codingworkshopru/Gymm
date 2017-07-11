package ru.codingworkshop.gymm.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.codingworkshop.gymm.data.ExerciseDifficulty;

/**
 * Created by Радик on 04.06.2017.
 */

public class Converters {
    @TypeConverter
    public static int fromEnumToInt(ExerciseDifficulty difficulty) {
        return difficulty.ordinal();
    }

    @TypeConverter
    public static ExerciseDifficulty fromIntToEnum(int difficulty) {
        return ExerciseDifficulty.values()[difficulty];
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public String convertToMapped(String value) {
        if (value == null || value.length() == 0)
            return value;

        Pattern pattern = Pattern.compile("^+", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(value);
        return matcher.replaceAll("\u2022 ");
    }
}
