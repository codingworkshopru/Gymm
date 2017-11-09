package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.common.BaseNodeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ProgramExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */
public final class ProgramExerciseLoader implements Loader<ProgramExerciseNode> {
    private final BaseNodeLoaderDelegate<ProgramExercise, ProgramSet> loaderDelegate;
    private ProgramExerciseNode node;

    public ProgramExerciseLoader(@NonNull ProgramExerciseNode node, @NonNull ProgramExerciseDataSource dataSource) {
        this.node = node;
        loaderDelegate = new BaseNodeLoaderDelegate<>(node, dataSource);
        loaderDelegate.addSource(dataSource.getExercise(), node::setExercise);
    }

    @Override
    public LiveData<ProgramExerciseNode> loadIt() {
        return loaderDelegate.mapLoaded(node);
    }
}
