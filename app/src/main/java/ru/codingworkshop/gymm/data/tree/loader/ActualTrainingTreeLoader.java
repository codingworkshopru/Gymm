package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ActualExercise;
import ru.codingworkshop.gymm.data.entity.ActualSet;
import ru.codingworkshop.gymm.data.entity.ActualTraining;
import ru.codingworkshop.gymm.data.tree.loader.builder.ActualTrainingTreeBuilder;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.OldLoader;
import ru.codingworkshop.gymm.data.tree.loader.common.TreeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ActualTrainingDataSource;
import ru.codingworkshop.gymm.data.tree.node.ActualTrainingTree;

/**
 * Created by Радик on 22.08.2017 as part of the Gymm project.
 */

public class ActualTrainingTreeLoader implements Loader<ActualTrainingTree>, OldLoader {
    private TreeLoaderDelegate<ActualTraining, ActualExercise, ActualSet> treeLoaderDelegate;
    private ActualTrainingTreeBuilder treeBuilder;

    public ActualTrainingTreeLoader(@NonNull ActualTrainingTreeBuilder treeBuilder, @NonNull ActualTrainingDataSource dataSource) {
        this.treeBuilder = treeBuilder;
        treeLoaderDelegate = new TreeLoaderDelegate<>(dataSource, treeBuilder);
    }

    @Override
    public LiveData<ActualTrainingTree> loadIt() {
        return null;
    }

    @Override
    public LiveData<Boolean> load() {
        return treeLoaderDelegate.getLoaded();
    }
}
