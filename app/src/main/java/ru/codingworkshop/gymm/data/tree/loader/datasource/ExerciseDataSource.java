package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

public class ExerciseDataSource extends NodeDataSource<Exercise, MuscleGroup> {
    private LiveData<MuscleGroup> primaryMuscleGroup;

    public ExerciseDataSource(ExercisesRepository repository, MuscleGroupsRepository muscleGroupsRepository, long exerciseId) {
        setParent(repository.getExerciseById(exerciseId));
        setChildren(muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(exerciseId));
        primaryMuscleGroup = Transformations.switchMap(getParent(), input -> {
            long primaryMuscleGroupId = input.getPrimaryMuscleGroupId();
            return muscleGroupsRepository.getMuscleGroupById(primaryMuscleGroupId);
        });
    }

    public LiveData<MuscleGroup> getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }
}
