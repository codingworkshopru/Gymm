package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
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
        setParent(programExercise);
    }
}
