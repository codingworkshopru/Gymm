package ru.codingworkshop.gymm.data.tree.node;

import java.util.Enumeration;
import java.util.Iterator;
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

    public ChildrenIterator childrenIterator() {
        return new ChildrenIterator(getChildren());
    }

    public GrandchildrenIterator grandchildrenIterator() {
        return new GrandchildrenIterator(getChildren());
    }

    public static class ChildrenIterator implements Iterator<ProgramExercise> {
        private Iterator<ProgramExerciseNode> iterator;

        public ChildrenIterator(List<ProgramExerciseNode> children) {
            this.iterator = children.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public ProgramExercise next() {
            return iterator.next().getParent();
        }
    }

    public static class GrandchildrenIterator implements Iterator<ProgramSet> {
        private Iterator<ProgramExerciseNode> iterator;
        private Iterator<ProgramSet> gcIterator;

        public GrandchildrenIterator(List<ProgramExerciseNode> children) {
            iterator = children.iterator();
            nextGc();
        }

        private void nextGc() {
            if (iterator.hasNext()) {
                gcIterator = iterator.next().getChildren().iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext() || gcIterator.hasNext();
        }

        @Override
        public ProgramSet next() {
            if (!gcIterator.hasNext()) {
                nextGc();
            }
            return gcIterator.next();
        }
    }
}
