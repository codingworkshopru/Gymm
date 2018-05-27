package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

public class PrimaryMuscleGroupQueryAdapter implements ModelQueryAdapter<MuscleGroup> {
    private MuscleGroupsRepository muscleGroupsRepository;

    public PrimaryMuscleGroupQueryAdapter(@NonNull MuscleGroupsRepository muscleGroupsRepository) {
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    @NonNull
    @Override
    public MuscleGroup query(long exerciseId) {
        return muscleGroupsRepository.getPrimaryMuscleGroupForExercise(exerciseId);
    }
}
