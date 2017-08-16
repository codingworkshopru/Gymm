package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramExerciseInterface;
import ru.codingworkshop.gymm.data.tree.ChildRestore;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;

/**
 * Created by Радик on 15.08.2017 as part of the Gymm project.
 */

public class MutableProgramTrainingTree extends ProgramTrainingTree implements ChildRestore {
    public MutableProgramTrainingTree() {
        super(new SortableRestoreChildrenHolder<>());
    }

    @Override
    public void restoreLastRemoved() {
        ((ChildRestore) getChildrenDelegate()).restoreLastRemoved();
    }

    @Override
    public void setChildren(List<? extends ProgramExerciseInterface> children) {
        List<ProgramExerciseNode> nodes = Lists.transform(children, c -> new MutableProgramExerciseNode((ProgramExercise) c));
        super.setChildren(nodes);
    }
}
