package ru.codingworkshop.gymm.data.wrapper;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.db.GymmDatabase;

/**
 * Created by Радик on 13.07.2017.
 */

public class ProgramExerciseWrapper {
    private Exercise exercise;
    private SortableChildrenDelegate<ProgramSet> childrenDelegate = new SortableChildrenDelegate<>();

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(@NonNull Exercise exercise) {
        Preconditions.checkArgument(
                exercise.getId() != GymmDatabase.INVALID_ID,
                "Phantom exercise setting"
        );

        this.exercise = exercise;
    }

    public List<ProgramSet> getProgramSets() {
        return childrenDelegate.getChildren();
    }

    public void setProgramSets(@NonNull Collection<ProgramSet> programSets) {
        childrenDelegate.setChildren(programSets);
    }
}
