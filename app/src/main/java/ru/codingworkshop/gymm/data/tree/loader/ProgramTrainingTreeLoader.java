package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ProgramTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.OldLoader;
import ru.codingworkshop.gymm.data.tree.loader.common.TreeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ProgramTrainingTree;

/**
 * Created by Радик on 16.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingTreeLoader implements Loader<ProgramTrainingTree>, OldLoader {
    private TreeLoaderDelegate<ProgramTraining, ProgramExercise, ProgramSet> treeLoaderDelegate;
    private ProgramTrainingTree tree;

    public ProgramTrainingTreeLoader(@NonNull ProgramTrainingTree tree, @NonNull ProgramTrainingDataSource dataSource) {
        this.tree = tree;
        ProgramTrainingTreeBuilder programTrainingTreeBuilder = new ProgramTrainingTreeBuilder(tree);
        treeLoaderDelegate = new TreeLoaderDelegate<>(dataSource, programTrainingTreeBuilder);
        treeLoaderDelegate.addSource(dataSource.getExercises(), programTrainingTreeBuilder::setExercises);
    }

    @Override
    public LiveData<ProgramTrainingTree> loadIt() {
        return treeLoaderDelegate.mapLoaded(tree);
    }

    @Override
    public LiveData<Boolean> load() {
        return treeLoaderDelegate.getLoaded();
    }
}