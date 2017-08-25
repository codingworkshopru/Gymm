package ru.codingworkshop.gymm.data.tree.loader.adapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 21.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeAdapter extends TreeAdapter<ProgramTraining, ProgramExercise, ProgramSet> {
    private Map<Long, Exercise> exercisesMap;
    private ProgramTrainingTree tree;

    public ProgramTrainingTreeAdapter(ProgramTrainingTree tree) {
        super(tree);
        this.tree = tree;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercisesMap = Maps.uniqueIndex(exercises, Exercise::getId);
    }

    @Override
    void setChildrenNodes(List<BaseNode<ProgramExercise, ProgramSet>> childrenNodes) {
        tree.setChildren(Lists.transform(childrenNodes, n -> (ProgramExerciseNode) n));
    }

    @Override
    void initNode(BaseNode<ProgramExercise, ProgramSet> node) {
        ProgramExerciseNode castedNode = (ProgramExerciseNode) node;
        castedNode.setExercise(exercisesMap.get(castedNode.getExerciseId()));
    }

    @Override
    BaseNode<ProgramExercise, ProgramSet> createChildNode(ProgramExercise child) {
        return tree.createChildNode(child);
    }

    @Override
    long getGrandchildParentId(ProgramSet grandchild) {
        return grandchild.getProgramExerciseId();
    }
}
