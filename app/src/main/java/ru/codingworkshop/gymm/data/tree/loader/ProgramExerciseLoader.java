package ru.codingworkshop.gymm.data.tree.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.loader.common.NodeLoaderDelegate;
import ru.codingworkshop.gymm.data.tree.node.ProgramExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ProgramExerciseAdapter;

/**
 * Created by Радик on 14.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseLoader implements Loader<ProgramExerciseNode> {
    private ProgramExerciseAdapter dataSource;

    @Inject
    public ProgramExerciseLoader(@NonNull ProgramExerciseAdapter dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public LiveData<ProgramExerciseNode> loadById(ProgramExerciseNode node, long id) {
        NodeLoaderDelegate<ProgramExercise, ProgramSet> loaderDelegate =
                new NodeLoaderDelegate<>(node, node, dataSource, dataSource, id);
        loaderDelegate.addSource(dataSource.getExercise(id), node::setExercise);

        return loaderDelegate.mapLoaded(node);
    }
}
