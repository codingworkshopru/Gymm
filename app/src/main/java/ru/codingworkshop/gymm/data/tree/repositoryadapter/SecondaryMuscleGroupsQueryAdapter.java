package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.List;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

public class SecondaryMuscleGroupsQueryAdapter implements ModelQueryAdapter<List<MuscleGroup>> {
    private MuscleGroupsRepository muscleGroupsRepository;

    public SecondaryMuscleGroupsQueryAdapter(@NonNull MuscleGroupsRepository muscleGroupsRepository) {
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    @NonNull
    @Override
    public List<MuscleGroup> query(long exerciseId) {
        return muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(exerciseId);
    }
}
