package ru.codingworkshop.gymm.data.tree.node;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ExerciseNode extends BaseNode<Exercise, MuscleGroup> {
    public ExerciseNode() {
        super(new ImmutableChildrenHolder<>());
    }
}
