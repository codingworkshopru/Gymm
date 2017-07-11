package ru.codingworkshop.gymm.data.model;

import android.arch.persistence.room.Delete;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.common.Model;

/**
 * Created by Радик on 04.06.2017.
 */

public interface ActualSet extends Model {
    long getActualExerciseId();
    void setActualExerciseId(long actualExerciseId);

    int getReps();
    void setReps(int reps);

    @Nullable Double getWeight();
    void setWeight(Double weight);
}
