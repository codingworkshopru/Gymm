package ru.codingworkshop.gymm.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.model.common.Model;

/**
 * Created by Радик on 04.06.2017.
 */

public interface ActualExercise extends Model {
    String getExerciseName();
    void setExerciseName(@NonNull String name);

    long getActualTrainingId();
    void setActualTrainingId(long actualTrainingId);

    @Nullable Long getProgramExerciseId();
    void setProgramExerciseId(Long programExerciseId);
}
