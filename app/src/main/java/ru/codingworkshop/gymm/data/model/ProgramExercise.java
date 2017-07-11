package ru.codingworkshop.gymm.data.model;

import ru.codingworkshop.gymm.data.model.common.Draftable;
import ru.codingworkshop.gymm.data.model.common.Model;
import ru.codingworkshop.gymm.data.model.common.Sortable;

/**
 * Created by Радик on 04.06.2017.
 */

public interface ProgramExercise extends Model, Sortable, Draftable {
    long getProgramTrainingId();
    void setProgramTrainingId(long programTrainingId);

    long getExerciseId();
    void setExerciseId(long exerciseId);
}
