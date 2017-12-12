package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.Preconditions2;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ImmutableProgramExerciseNode extends ProgramExerciseNode {
    public ImmutableProgramExerciseNode() {
        super(new ImmutableChildrenHolder<>());
    }

    public ImmutableProgramExerciseNode(ProgramExercise programExercise) {
        this();
        super.setParent(programExercise);
    }

    @Override
    public void setParent(ProgramExercise parent) {
        Preconditions2.checkIsNull(getParent(), "Immutable node already has a parent");
        super.setParent(parent);
    }

    @Override
    public void setExercise(Exercise exercise) {
        Preconditions2.checkIsNull(getExercise(), "Immutable node already has an exercise");
        super.setExercise(exercise);
    }

    @Override
    public void setSortOrder(int sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProgramTrainingId(long programTrainingId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExerciseId(long exerciseId) {
        throw new UnsupportedOperationException();
    }
}
