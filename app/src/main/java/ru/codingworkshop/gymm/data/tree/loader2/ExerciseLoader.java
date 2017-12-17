package ru.codingworkshop.gymm.data.tree.loader2;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.Single;
import ru.codingworkshop.gymm.data.tree.loader.common.Loader;
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

    @SuppressLint("CheckResult")
    @Override
    public LiveData<ExerciseNode> loadById(ExerciseNode node, long id) {
        MutableLiveData<ExerciseNode> liveExerciseNode = new MutableLiveData<>();

        Single.zip(
                Single.just(node),
                exerciseAdapter.getParent(id),
                exerciseAdapter.getChildren(id).toSingle(Collections.emptyList()),
                exerciseAdapter.getPrimaryMuscleGroup(id),
                (exerciseNode, exercise, muscleGroups, muscleGroup) -> {
                    exerciseNode.setParent(exercise);
                    exerciseNode.setChildren(muscleGroups);
                    exerciseNode.setPrimaryMuscleGroup(muscleGroup);
                    return exerciseNode;
                })
                .subscribe(liveExerciseNode::postValue);
        return liveExerciseNode;
    }
}
