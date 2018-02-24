package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.repository.ExercisesRepository;

public class ExerciseQueryAdapter implements ModelQueryAdapter<Flowable<Exercise>> {
    private ExercisesRepository exercisesRepository;

    public ExerciseQueryAdapter(@NonNull ExercisesRepository exercisesRepository) {
        this.exercisesRepository = exercisesRepository;
    }

    @NonNull
    @Override
    public Flowable<Exercise> query(long parentId) {
        return exercisesRepository.getExerciseById(parentId);
    }
}
