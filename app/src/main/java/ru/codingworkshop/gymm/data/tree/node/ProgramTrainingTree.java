package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;

/**
 * Created by Радик on 15.08.2017 as part of the Gymm project.
 */

public abstract class ProgramTrainingTree extends BaseNode<ProgramTraining, ProgramExerciseNode> {
    public ProgramTrainingTree(ChildrenHolder<ProgramExerciseNode> childrenDelegate) {
        super(childrenDelegate);
    }

    public abstract ProgramExerciseNode createChildNode(ProgramExercise programExercise);
}
