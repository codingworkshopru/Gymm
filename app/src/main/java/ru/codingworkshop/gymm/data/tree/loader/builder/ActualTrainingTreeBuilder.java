package ru.codingworkshop.gymm.data.tree.loader.builder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 25.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeBuilder extends TreeBuilder<ActualTraining, ActualExercise, ActualSet> {
    private ProgramTrainingTree programTrainingTree;
    private ActualTrainingTree tree;

    public ActualTrainingTreeBuilder(ActualTrainingTree tree) {
        super(tree);
        this.tree = tree;
    }

    public ActualTrainingTreeBuilder setProgramTrainingTree(ProgramTrainingTree programTrainingTree) {
        this.programTrainingTree = programTrainingTree;
        return this;
    }

    @Override
    public BaseNode<ActualTraining, ? extends BaseNode<ActualExercise, ActualSet>> build() {
        Preconditions.checkNotNull(programTrainingTree, "Program training tree must be set");
        if (getParent() == null) {
            ProgramTraining training = programTrainingTree.getParent();
            ActualTraining actualTraining = new ActualTraining(training.getId(), training.getName());
            setParent(actualTraining);
        }
        return super.build();
    }

    @Override
    protected BaseNode<ActualExercise, ActualSet> getNode(ActualExercise child) {
        return new ActualExerciseNode(child);
    }

    public ActualTrainingTree getTree() {
        return tree;
    }

    @Override
    protected void setNodes(List<? extends BaseNode<ActualExercise, ActualSet>> baseNodes) {
        Map<Long, ? extends BaseNode<ActualExercise, ActualSet>> nodeProgramExerciseIdMap
                = Maps.uniqueIndex(baseNodes, bn -> bn.getParent().getProgramExerciseId());

        List<ActualExerciseNode> nodes = Lists.newArrayListWithCapacity(programTrainingTree.getChildren().size());
        for (ProgramExerciseNode exerciseNode : programTrainingTree.getChildren()) {
            ActualExerciseNode node = (ActualExerciseNode) nodeProgramExerciseIdMap.get(exerciseNode.getId());
            if (node == null) {
                node = new ActualExerciseNode();
            }
            node.setProgramExerciseNode(exerciseNode);
            nodes.add(node);
        }
        tree.setChildren(nodes);
    }

    @Override
    protected void beforeBuild() {
        tree.setProgramTraining(programTrainingTree.getParent());
    }

    @Override
    protected long parentGetter(ActualSet grandchild) {
        return grandchild.getActualExerciseId();
    }
}
