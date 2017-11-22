package ru.codingworkshop.gymm.data.entity;

import javax.annotation.Nullable;

import ru.codingworkshop.gymm.data.entity.common.Draftable;
import ru.codingworkshop.gymm.data.entity.common.Model;
import ru.codingworkshop.gymm.data.entity.common.Sortable;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public interface IProgramExercise extends Model, Sortable, Draftable {
    long getProgramTrainingId();

    void setProgramTrainingId(long programTrainingId);

    @Nullable
    Long getExerciseId();

    void setExerciseId(@Nullable Long exerciseId);
}
