package ru.codingworkshop.gymm.data.model;

import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.common.Model;
import ru.codingworkshop.gymm.data.model.common.Sortable;

/**
 * Created by Радик on 04.06.2017.
 */

public interface ProgramSet extends Model, Sortable {
    long getProgramExerciseId();
    void setProgramExerciseId(long programExerciseId);

    int getReps();
    void setReps(int reps);

    @Nullable Integer getSecondsForRest();
    void setSecondsForRest(Integer secondForRest);
}
