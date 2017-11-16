package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.v4.util.Preconditions;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.TreeLoaderDelegate;
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
    public LiveData<ActualTrainingTree> load(ActualTrainingTree tree) {
        return null;
    }

    @Override
    public LiveData<ActualTrainingTree> loadById(ActualTrainingTree tree, long id) {
        return Transformations.switchMap(dataSource.getParent(id), actualTraining -> {
            ProgramTrainingTree programTrainingTree = new ImmutableProgramTrainingTree();
            return Transformations.switchMap(programTrainingTreeLoader.loadById(programTrainingTree, actualTraining.getProgramTrainingId()), trainingTree -> {
                ActualTrainingTreeBuilder actualTrainingTreeBuilder = new ActualTrainingTreeBuilder(tree)
                        .setProgramTrainingTree(trainingTree);
                TreeLoaderDelegate<ActualTraining, ActualExercise, ActualSet> treeLoaderDelegate =
                        new TreeLoaderDelegate<>(actualTrainingTreeBuilder, dataSource, dataSource, dataSource, id);

                return treeLoaderDelegate.mapLoaded(tree);
            });
        });
    }
}
