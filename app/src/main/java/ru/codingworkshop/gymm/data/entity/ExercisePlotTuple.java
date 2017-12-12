package ru.codingworkshop.gymm.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import ru.codingworkshop.gymm.db.Converters;

/**
 * Created by Radik on 10.12.2017.
 */

@TypeConverters(Converters.class)
public class ExercisePlotTuple {
    @ColumnInfo(name = "trainingTime")
    private Date trainingTime;

    @ColumnInfo(name = "reps")
    private int reps;

    @ColumnInfo(name = "weight")
    private Double weight;

    public Date getTrainingTime() {
        return trainingTime;
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
