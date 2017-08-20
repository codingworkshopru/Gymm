package ru.codingworkshop.gymm.data.tree.saver;

import java.util.Collection;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseSaver implements Saver {
    private ProgramTrainingRepository repository;
    private ProgramExerciseNode node;

    public ProgramExerciseSaver(ProgramExerciseNode node, ProgramTrainingRepository repository) {
        this.node = node;
        this.repository = repository;
    }

    @Override
    public void save() {
        new AbstractParentSaver<ProgramExercise>(node.getParent()) {
            @Override
            public void updateParent(ProgramExercise parent) {
                repository.updateProgramExercise(parent);
            }

            @Override
            public void insertParent(ProgramExercise parent) {
                repository.insertProgramExercise(parent);
            }
        }
        .save();

        new AbstractChildrenSaver<ProgramSet>(node.getChildren(), repository.getProgramSetsForExercise(node.getParent())) {
            @Override
            public boolean objectsAreSame(ProgramSet child1, ProgramSet child2) {
                return child1.getId() == child2.getId();
            }

            @Override
            public boolean contentsAreEqual(ProgramSet child1, ProgramSet child2) {
                return child1.equals(child2);
            }

            @Override
            public void insertChildren(Collection<ProgramSet> children) {
                repository.insertProgramSets(children);
            }

            @Override
            public void updateChildren(Collection<ProgramSet> children) {
                repository.updateProgramSets(children);
            }

            @Override
            public void deleteChildren(Collection<ProgramSet> children) {
                repository.deleteProgramSets(children);
            }
        }
        .save();
    }
}
