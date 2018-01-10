package ru.codingworkshop.gymm.data.tree.loader.builder;

import java.util.ArrayList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ImmutableActualTrainingTree;

public class ImmutableActualTrainingTreeBuilder extends TreeBuilder<ActualTraining, ActualExercise, ActualSet> {

    private ImmutableActualTrainingTree tree;

    public ImmutableActualTrainingTreeBuilder(ImmutableActualTrainingTree tree) {
        super(tree);
        this.tree = tree;
    }

    @Override
    protected BaseNode<ActualExercise, ActualSet> getNode(ActualExercise child) {
        ImmutableActualExerciseNode node = new ImmutableActualExerciseNode();
        node.setParent(child);
        return node;
    }

    @Override
    protected void setNodes(List<? extends BaseNode<ActualExercise, ActualSet>> baseNodes) {
        List<ImmutableActualExerciseNode> nodes = new ArrayList<>(baseNodes.size());
        for (BaseNode<ActualExercise, ActualSet> node : baseNodes) {
            ImmutableActualExerciseNode castedNode = (ImmutableActualExerciseNode) node;

            double volume = 0.0;
            for (ActualSet set : node.getChildren()) {
                Double weight = set.getWeight();
                volume += set.getReps() * (weight != null ? weight : 0.0);
            }
            castedNode.setVolume(volume);

            nodes.add(castedNode);
        }
        tree.setChildren(nodes);
    }

    @Override
    protected long parentGetter(ActualSet grandchild) {
        return grandchild.getActualExerciseId();
    }
}
