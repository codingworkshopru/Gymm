package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.TypeConverters;

import java.util.Calendar;
import java.util.Date;

import ru.codingworkshop.gymm.db.Converters;
import timber.log.Timber;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 * Created by Radik on 10.12.2017.
 */

@TypeConverters(Converters.class)
public class ExercisePlotTuple {
    private Date trainingTime;
    private int reps;
    private Double weight;

    public Date getTrainingTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(trainingTime);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.clear(MINUTE);
        c.clear(SECOND);
        c.clear(MILLISECOND);
        return c.getTime();
    }

    public void setTrainingTime(Date trainingTime) {
        this.trainingTime = trainingTime;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
