package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExerciseInterface;
import ru.codingworkshop.gymm.data.entity.common.Sortable;
import ru.codingworkshop.gymm.data.tree.ChildRestore;
import ru.codingworkshop.gymm.data.tree.holder.SortableRestoreChildrenHolder;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseSortableRestoreChildrenNode extends ProgramExerciseNode implements Sortable, ProgramExerciseInterface, ChildRestore {

    public ProgramExerciseSortableRestoreChildrenNode() {
        super(new SortableRestoreChildrenHolder<>());
    }

    @Override
    public void restoreLastRemoved() {
        ((ChildRestore) getChildrenDelegate()).restoreLastRemoved();
    }
}
