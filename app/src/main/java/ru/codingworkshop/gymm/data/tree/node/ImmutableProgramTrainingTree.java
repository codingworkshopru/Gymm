package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ImmutableProgramTrainingTree extends AbstractProgramTrainingTree {
    public ImmutableProgramTrainingTree() {
        super(new ImmutableChildrenHolder<>());
    }

    @Override
    public ProgramExerciseNode createChildNode(ProgramExercise programExercise) {
        return new ImmutableProgramExerciseNode(programExercise);
    }
}
