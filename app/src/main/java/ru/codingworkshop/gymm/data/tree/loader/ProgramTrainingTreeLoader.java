package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.loader.builder.ProgramTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramTrainingAdapter;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader implements Loader<ProgramTrainingTree> {
    private ProgramTrainingAdapter dataSource;

    @Inject
    public ProgramTrainingTreeLoader(@NonNull ProgramTrainingAdapter dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Flowable<ProgramTrainingTree> loadById(ProgramTrainingTree tree, long id) {

        return Flowable.zip(
                dataSource.getParent(id),
                dataSource.getChildren(id),
                dataSource.getGrandchildren(id),
                dataSource.getExercises(id),
                (programTraining, programExercises, programSets, exercises) -> {
                    ProgramTrainingTreeBuilder builder = new ProgramTrainingTreeBuilder(tree);
                    builder.setParent(programTraining);
                    builder.setChildren(programExercises);
                    builder.setGrandchildren(programSets);
                    builder.setExercises(exercises);
                    return (ProgramTrainingTree) builder.build();
                }
        );
    }
}