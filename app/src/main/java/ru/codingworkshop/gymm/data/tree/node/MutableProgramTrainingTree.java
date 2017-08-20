package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.ChildRestore;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;

/**
 * Created by Радик on 15.08.2017 as part of the Gymm project.
 */

public class MutableProgramTrainingTree extends AbstractProgramTrainingTree implements ChildRestore {
    public MutableProgramTrainingTree() {
        super(new SortableRestoreChildrenHolder<>());
    }

    @Override
    public void restoreLastRemoved() {
        ((ChildRestore) getChildrenDelegate()).restoreLastRemoved();
    }

    @Override
    public void setProgramExercises(List<ProgramExercise> programExercises) {
        setChildren(Lists.transform(programExercises, MutableProgramExerciseNode::new));
    }
}
