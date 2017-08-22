package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.ActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeAdapter extends TreeAdapter<ActualTraining, ActualExercise, ActualSet> {
    private ActualTrainingTree tree;
    private Map<Long, ProgramExerciseNode> programExerciseNodes;

    public ActualTrainingTreeAdapter(ActualTrainingTree tree, @NonNull ProgramTrainingTree programTrainingTree) {
        super(tree);
        this.tree = tree;

        tree.setProgramTraining(Preconditions.checkNotNull(programTrainingTree.getParent()));

        Preconditions.checkArgument(programTrainingTree.hasChildren());
        this.programExerciseNodes = Maps.uniqueIndex(programTrainingTree.getChildren(), n -> n.getParent().getId());

        for (ProgramExerciseNode n : programTrainingTree.getChildren()) {
            Preconditions.checkNotNull(n.getExercise());
            Preconditions.checkArgument(n.hasChildren());
        }
    }

    @Override
    void setChildrenNodes(List<BaseNode<ActualExercise, ActualSet>> childrenNodes) {
        tree.setChildren(Lists.transform(childrenNodes, n -> (ActualExerciseNode) n));
    }

    @Override
    void initNode(BaseNode<ActualExercise, ActualSet> node) {
        ActualExerciseNode n = (ActualExerciseNode) node;
        n.setProgramExerciseNode(programExerciseNodes.get(n.getParent().getProgramExerciseId()));
    }

    @Override
    BaseNode<ActualExercise, ActualSet> createChildNode(ActualExercise child) {
        return new ActualExerciseNode(child);
    }

    @Override
    long getGrandchildParentId(ActualSet grandchild) {
        return grandchild.getActualExerciseId();
    }
}
