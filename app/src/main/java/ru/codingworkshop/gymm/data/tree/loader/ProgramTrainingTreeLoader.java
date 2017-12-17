package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ProgramTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.TreeLoaderDelegate;
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
    public LiveData<ProgramTrainingTree> loadById(ProgramTrainingTree tree, long id) {
        ProgramTrainingTreeBuilder programTrainingTreeBuilder = new ProgramTrainingTreeBuilder(tree);
        TreeLoaderDelegate<ProgramTraining, ProgramExercise, ProgramSet> treeLoaderDelegate =
                new TreeLoaderDelegate<>(programTrainingTreeBuilder, dataSource, dataSource, dataSource, id);
        treeLoaderDelegate.addSource(dataSource.getExercises(id), programTrainingTreeBuilder::setExercises);

        return treeLoaderDelegate.mapLoaded(tree);
    }
}