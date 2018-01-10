package ru.codingworkshop.gymm.data.tree.loader.builder;

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
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeBuilder extends TreeBuilder<ProgramTraining, ProgramExercise, ProgramSet> {
    private ProgramTrainingTree tree;
    private Map<Long, Exercise> exerciseMap;

    public ProgramTrainingTreeBuilder(ProgramTrainingTree tree) {
        super(tree);
        this.tree = tree;
    }

    public ProgramTrainingTreeBuilder setExercises(List<Exercise> exercises) {
        exerciseMap = Maps.uniqueIndex(exercises, Exercise::getId);
        return this;
    }

    @Override
    protected BaseNode<ProgramExercise, ProgramSet> getNode(ProgramExercise child) {
        ProgramExerciseNode node = tree.createChildNode(child);
        node.setExercise(exerciseMap.get(child.getExerciseId()));
        return node;
    }

    @Override
    protected void setNodes(List<? extends BaseNode<ProgramExercise, ProgramSet>> baseNodes) {
        tree.setChildren(Lists.transform(baseNodes, bn -> (ProgramExerciseNode) bn));
    }

    @Override
    protected long parentGetter(ProgramSet grandchild) {
        return grandchild.getProgramExerciseId();
    }
}
