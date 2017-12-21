package ru.codingworkshop.gymm.data.tree.loader;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ImmutableProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ActualTrainingAdapter;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeLoader implements Loader<ActualTrainingTree> {
    private ActualTrainingAdapter dataSource;
    private ProgramTrainingTreeLoader programTrainingTreeLoader;

    @Inject
    public ActualTrainingTreeLoader(@NonNull ActualTrainingAdapter dataSource, @NonNull ProgramTrainingTreeLoader programTrainingTreeLoader) {
        this.dataSource = dataSource;
        this.programTrainingTreeLoader = programTrainingTreeLoader;
    }

    @Override
    public Flowable<ActualTrainingTree> loadById(ActualTrainingTree tree, long id) {
        ActualTrainingTreeBuilder actualTrainingTreeBuilder = new ActualTrainingTreeBuilder(tree);
        Flowable<ActualTraining> parentFlowable = dataSource.getParent(id);
        return Flowable.zip(
                Flowable.just(actualTrainingTreeBuilder),
                parentFlowable,
                dataSource.getChildren(id),
                dataSource.getGrandchildren(id),
                parentFlowable.switchMap(actualTraining -> {
                    ProgramTrainingTree programTrainingTree = new ImmutableProgramTrainingTree();
                    return programTrainingTreeLoader.loadById(programTrainingTree, actualTraining.getProgramTrainingId());
                }),
                (builder, actualTraining, actualExercises, actualSets, programTrainingTree) -> {
                    builder.setParent(actualTraining);
                    builder.setChildren(actualExercises);
                    builder.setGrandchildren(actualSets);
                    builder.setProgramTrainingTree(programTrainingTree);
                    return (ActualTrainingTree) builder.build();
                }
        );
    }
}
