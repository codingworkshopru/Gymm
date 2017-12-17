package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.NodeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ExerciseAdapter;

/**
 * Created by Радик on 18.08.2017 as part of the Gymm project.
 */

public class ExerciseLoader implements Loader<ExerciseNode> {
    private ExerciseAdapter dataSource;

    @Inject
    public ExerciseLoader(@NonNull ExerciseAdapter dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public LiveData<ExerciseNode> loadById(ExerciseNode node, long id) {
        NodeLoaderDelegate<Exercise, MuscleGroup> nodeLoaderDelegate =
                new NodeLoaderDelegate<>(node, node, dataSource, dataSource, id);
        nodeLoaderDelegate.addSource(dataSource.getPrimaryMuscleGroup(id), node::setPrimaryMuscleGroup);

        return nodeLoaderDelegate.mapLoaded(node);
    }
}
