package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.IProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public abstract class ProgramExerciseNode extends BaseNode<ProgramExercise, ProgramSet> implements IProgramExercise {
    private Exercise exercise;

    public ProgramExerciseNode(ChildrenHolder<ProgramSet> childrenDelegate) {
        super(childrenDelegate);
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;

        if (getParent() == null || exercise == null) return;

        long exerciseId = exercise.getId();
        if (getExerciseId() != exerciseId) {
            setExerciseId(exerciseId);
        }
    }

    @Override
    public int getSortOrder() {
        return getParent().getSortOrder();
    }

    @Override
    public void setSortOrder(int sortOrder) {
        getParent().setSortOrder(sortOrder);
    }

    @Override
    public long getId() {
        return getParent().getId();
    }

    @Override
    public void setId(long id) {
        getParent().setId(id);
    }

    @Override
    public long getProgramTrainingId() {
        return getParent().getProgramTrainingId();
    }

    @Override
    public void setProgramTrainingId(long programTrainingId) {
        getParent().setProgramTrainingId(programTrainingId);
    }

    @Override
    public long getExerciseId() {
        ProgramExercise parent = getParent();
        return parent != null ? parent.getExerciseId() : 0L;
    }

    @Override
    public void setExerciseId(long exerciseId) {
        if (getParent() != null) {
            getParent().setExerciseId(exerciseId);
        }
    }
}
