package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.ProgramExerciseInterface;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public abstract class ProgramTrainingNode extends BaseNode<ProgramTraining, ProgramExerciseInterface> {
    public ProgramTrainingNode(ChildrenHolder<ProgramExerciseInterface> childrenDelegate) {
        super(childrenDelegate);
    }
}
