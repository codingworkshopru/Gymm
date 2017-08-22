package ru.codingworkshop.gymm.data.tree.saver;

import android.arch.lifecycle.LiveData;

import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.List;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.BaseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class AnotherOneProgramTrainingSaver implements Saver {
    private ProgramTrainingTree tree;
    private ProgramTrainingRepository repository;

    public AnotherOneProgramTrainingSaver(ProgramTrainingTree tree, ProgramTrainingRepository repository) {
        this.tree = tree;
        this.repository = repository;
    }

    @Override
    public void save() {
        new AbstractParentSaver<ProgramTraining>(tree.getParent()) {
            @Override
            public void updateParent(ProgramTraining parent) {
                repository.updateProgramTraining(parent);
            }

            @Override
            public void insertParent(ProgramTraining parent) {
                repository.insertProgramTraining(parent);
            }
        }
        .save();

        Collection<ProgramExercise> exercises = transformFromNodes(tree.getChildren());
        LiveData<List<ProgramExercise>> oldExercises = repository.getProgramExercisesForTraining(tree.getParent());
        new AbstractChildrenSaver<ProgramExercise>(exercises, oldExercises) {
            @Override
            public boolean objectsAreSame(ProgramExercise child1, ProgramExercise child2) {
                return child1.getId() == child2.getId();
            }

            @Override
            public boolean contentsAreEqual(ProgramExercise child1, ProgramExercise child2) {
                return child1.getSortOrder() == child2.getSortOrder();
            }

            @Override
            public void insertChildren(Collection<ProgramExercise> children) {

            }

            @Override
            public void updateChildren(Collection<ProgramExercise> children) {
                repository.updateProgramExercises(children);
            }

            @Override
            public void deleteChildren(Collection<ProgramExercise> children) {
                repository.deleteProgramExercises(children);
            }
        }
        .save();
    }

    private Collection<ProgramExercise> transformFromNodes(Collection<ProgramExerciseNode> nodes) {
        return Collections2.transform(nodes, BaseNode::getParent);
    }
}
