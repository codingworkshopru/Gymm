package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

public class SecondaryMuscleGroupsQueryAdapter implements ModelQueryAdapter<Flowable<List<MuscleGroup>>> {
    private MuscleGroupsRepository muscleGroupsRepository;

    public SecondaryMuscleGroupsQueryAdapter(@NonNull MuscleGroupsRepository muscleGroupsRepository) {
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    @NonNull
    @Override
    public Flowable<List<MuscleGroup>> query(long exerciseId) {
        return muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(exerciseId);
    }
}
