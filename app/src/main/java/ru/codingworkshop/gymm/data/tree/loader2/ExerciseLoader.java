package ru.codingworkshop.gymm.data.tree.loader2;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.tree.node.ExerciseNode;
import ru.codingworkshop.gymm.data.tree.repositoryadapter2.ExerciseAdapter;

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
        return Flowable.zip(
                Flowable.just(node),
                exerciseAdapter.getParent(id),
                exerciseAdapter.getChildren(id),
                exerciseAdapter.getPrimaryMuscleGroup(id),
                (exerciseNode, exercise, children, muscleGroup) -> {
                    exerciseNode.setParent(exercise);
                    exerciseNode.setChildren(children);
                    exerciseNode.setPrimaryMuscleGroup(muscleGroup);
                    return exerciseNode;
                });
    }
}
