package ru.codingworkshop.gymm.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.codingworkshop.gymm.data.ExerciseDifficulty;
import ru.codingworkshop.gymm.data.model.common.NamedModel;

/**
 * Created by Радик on 22.05.2017.
 */

public interface Exercise extends NamedModel {
    ExerciseDifficulty getDifficulty();
    void setDifficulty(@NonNull ExerciseDifficulty difficulty);

    long getPrimaryMuscleGroupId();
    void setPrimaryMuscleGroupId(long primaryMuscleGroupId);

    boolean isWithWeight();
    void setWithWeight(boolean withWeight);

    @Nullable String getYouTubeVideo();
    void setYouTubeVideo(String youTubeVideo);

    @Nullable String getSteps();
    void setSteps(String steps);

    @Nullable String getCaution();
    void setCaution(String caution);

    @Nullable String getAdvices();
    void setAdvices(String advices);

    @Nullable String getVariations();
    void setVariations(String variations);
}
