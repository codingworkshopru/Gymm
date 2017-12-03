package ru.codingworkshop.gymm.data.tree.node;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.holder.ChildrenHolder;

/**
 * Created by Радик on 15.08.2017 as part of the Gymm project.
 */

public abstract class ProgramTrainingTree extends BaseNode<ProgramTraining, ProgramExerciseNode> {
    public ProgramTrainingTree(ChildrenHolder<ProgramExerciseNode> childrenDelegate) {
        super(childrenDelegate);
    }

    public abstract ProgramExerciseNode createChildNode(ProgramExercise programExercise);

    public List<ProgramExercise> getProgramExercises() {
        return Lists.transform(getChildren(), BaseNode::getParent);
    }

    public List<ProgramSet> getAllProgramSets() {
        List<ProgramSet> sets = new LinkedList<>();
        for (ProgramExerciseNode n : getChildren()) {
            sets.addAll(n.getChildren());
        }

        return sets;
    }
}
