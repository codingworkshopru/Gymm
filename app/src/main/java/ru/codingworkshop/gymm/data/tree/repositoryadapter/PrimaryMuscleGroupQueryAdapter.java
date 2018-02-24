package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

public class PrimaryMuscleGroupQueryAdapter implements ModelQueryAdapter<Flowable<MuscleGroup>> {
    private MuscleGroupsRepository muscleGroupsRepository;

    public PrimaryMuscleGroupQueryAdapter(@NonNull MuscleGroupsRepository muscleGroupsRepository) {
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    @NonNull
    @Override
    public Flowable<MuscleGroup> query(long exerciseId) {
        return muscleGroupsRepository.getPrimaryMuscleGroupForExercise(exerciseId);
    }
}
