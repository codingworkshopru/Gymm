package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ImmutableProgramTrainingTree extends ProgramTrainingTree {
    public ImmutableProgramTrainingTree() {
        super(new SimpleChildrenHolder<>());
    }
}
