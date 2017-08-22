package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.Preconditions2;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

// TODO make it real immutable (without such setters)
public class ImmutableProgramTrainingTree extends ProgramTrainingTree {
    public ImmutableProgramTrainingTree() {
        super(new ImmutableChildrenHolder<>());
    }

    @Override
    public ProgramExerciseNode createChildNode(ProgramExercise programExercise) {
        return new ImmutableProgramExerciseNode(programExercise);
    }

    @Override
    public void setParent(ProgramTraining parent) {
        Preconditions2.checkIsNull(getParent());
        super.setParent(parent);
    }
}
