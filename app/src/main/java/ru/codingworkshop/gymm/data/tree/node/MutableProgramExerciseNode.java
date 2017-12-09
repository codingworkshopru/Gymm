package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
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
        super.setParent(parent);
    }

    public MutableProgramExerciseNode(ProgramExerciseNode that) {
        this();
        ProgramExercise parent = that.getParent();
        setParent(parent != null ? new ProgramExercise(parent) : null);
        setChildren(Lists.transform(that.getChildren(), ProgramSet::new));
        setExercise(that.getExercise());
    }
}
