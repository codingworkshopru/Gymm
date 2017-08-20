package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.tree.holder.ImmutableChildrenHolder;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ImmutableProgramTrainingTree extends AbstractProgramTrainingTree {
    public ImmutableProgramTrainingTree() {
        super(new ImmutableChildrenHolder<>());
    }

    @Override
    public void setProgramExercises(List<ProgramExercise> programExercises) {
        super.setChildren(Lists.transform(programExercises, ImmutableProgramExerciseNode::new));
    }
}
