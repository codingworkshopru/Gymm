package ru.codingworkshop.gymm.data.tree.loader;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter.ExerciseAdapter;

/**
 * Created by Radik on 17.12.2017.
 */

public class ExerciseLoader implements Loader<ExerciseNode> {
    private ExerciseAdapter exerciseAdapter;

    @Inject
    ExerciseLoader(ExerciseAdapter exerciseAdapter) {
        this.exerciseAdapter = exerciseAdapter;
    }

    @Override
    public Flowable<ExerciseNode> loadById(ExerciseNode node, long id) {
        return Flowable.just(node)
                .map(exerciseNode -> {
                    exerciseNode.setParent(exerciseAdapter.getParent(id));
                    exerciseNode.setChildren(exerciseAdapter.getChildren(id));
                    exerciseNode.setPrimaryMuscleGroup(exerciseAdapter.getPrimaryMuscleGroup(id));
                    return exerciseNode;
                });
    }
}
