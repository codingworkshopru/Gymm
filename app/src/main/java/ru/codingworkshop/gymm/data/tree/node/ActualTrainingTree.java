package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.holder.SimpleChildrenHolder;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTree extends BaseNode<ActualTraining, ActualExerciseNode> implements ActualTrainingTreeInterface {
    private ProgramTrainingTree programTrainingTree;

    public ActualTrainingTree() {
        super(new SimpleChildrenHolder<>());
        programTrainingTree = new ImmutableProgramTrainingTree();
    }

    public ProgramTrainingTree getProgramTrainingTree() {
        return programTrainingTree;
    }

    public void setProgramTrainingTree(ProgramTrainingTree programTrainingTree) {
        this.programTrainingTree = programTrainingTree;
    }

    @Override
    public void setActualExercises(List<ActualExercise> actualExercises) {
        setChildren(Lists.transform(actualExercises, ActualExerciseNode::new));
    }
}
