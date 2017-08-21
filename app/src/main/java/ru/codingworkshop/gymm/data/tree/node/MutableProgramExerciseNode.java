package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;

/**
 * Created by Радик on 15.08.2017 as part of the Gymm project.
 */

public class MutableProgramExerciseNode extends ProgramExerciseNode {
    public MutableProgramExerciseNode() {
        super(new SortableRestoreChildrenHolder<>());
    }

    public MutableProgramExerciseNode(ProgramExercise parent) {
        this();
        setParent(parent);
    }
}
