package ru.codingworkshop.gymm.data.tree.saver;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Completable;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExercisesAlterAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExercisesQueryAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramSetsAlterAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramSetsQueryAdapter;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAlterAdapter;
import ru.codingworkshop.gymm.db.GymmDatabase;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Radik on 29.11.2017.
 */

public class ProgramTrainingTreeSaver implements Saver<ProgramTrainingTree> {
    private ProgramTrainingRepository programTrainingRepository;

    @Inject
    ProgramTrainingTreeSaver(ProgramTrainingRepository programTrainingRepository) {
        this.programTrainingRepository = programTrainingRepository;
    }

    @Override
    public Completable save(@NonNull ProgramTrainingTree tree) {
        return Completable.fromRunnable(() -> saveTree(tree));
    }

    private void saveTree(ProgramTrainingTree tree) {
        ProgramTrainingAlterAdapter programTrainingAlterAdapter = new ProgramTrainingAlterAdapter(programTrainingRepository);
        ModelSaver<ProgramTraining> programTrainingModelSaver = new ModelSaver<>(programTrainingAlterAdapter);
        ProgramTraining parent = tree.getParent();
        programTrainingModelSaver.save(parent);

        setParentIdsToExercises(tree);
        ChildrenSaver<ProgramExercise> programExerciseChildrenSaver = new ChildrenSaver<>(
                new ProgramExercisesQueryAdapter(programTrainingRepository),
                new ProgramExercisesAlterAdapter(programTrainingRepository),
                parent.getId());
        programExerciseChildrenSaver.save(tree.getProgramExercises());

        setParentIdsToSets(tree);
        ChildrenSaver<ProgramSet> programSetChildrenSaver = new ChildrenSaver<>(
                new ProgramSetsQueryAdapter(programTrainingRepository),
                new ProgramSetsAlterAdapter(programTrainingRepository),
                parent.getId());
        programSetChildrenSaver.save(tree.getAllProgramSets());
    }

    /**
     * call after parent save
     */
    private void setParentIdsToExercises(@NonNull ProgramTrainingTree tree) {
        ProgramTraining parent = tree.getParent();
        Preconditions.checkArgument(GymmDatabase.isValidId(parent), "parent id is not valid");

        for (ProgramExerciseNode node : tree.getChildren()) {
            if (!GymmDatabase.isValidId(node.getProgramTrainingId())) {
                node.setProgramTrainingId(parent.getId());
            }
        }
    }

    /**
     * call after exercises save
     */
    private void setParentIdsToSets(@NonNull ProgramTrainingTree tree) {
        for (ProgramExerciseNode node : tree.getChildren()) {
            Preconditions.checkArgument(GymmDatabase.isValidId(node));
            for (ProgramSet set : node.getChildren()) {
                if (!GymmDatabase.isValidId(set.getProgramExerciseId())) {
                    set.setProgramExerciseId(node.getId());
                }
            }
        }
    }
}
