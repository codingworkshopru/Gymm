package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.loader.common.BaseNodeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.datasource.ExerciseDataSource;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ExerciseLoader implements Loader<ExerciseNode> {
    private BaseNodeLoaderDelegate<Exercise, MuscleGroup> baseNodeLoaderDelegate;
    private ExerciseNode node;

    public ExerciseLoader(@NonNull ExerciseNode node, @NonNull ExerciseDataSource dataSource) {
        this.node = node;
        baseNodeLoaderDelegate = new BaseNodeLoaderDelegate<>(node, dataSource);
        baseNodeLoaderDelegate.addSource(dataSource.getPrimaryMuscleGroup(), node::setPrimaryMuscleGroup);
    }

    @Override
    public LiveData<ExerciseNode> loadIt() {
        return baseNodeLoaderDelegate.mapLoaded(node);
    }
}
