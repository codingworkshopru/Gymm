package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.repository.ExercisesRepository;

/**
 * Created by Radik on 21.11.2017.
 */

public class ExerciseListDialogFragmentViewModel extends ViewModel {
    private ExercisesRepository exercisesRepository;
    private LiveData<List<Exercise>> liveExercises;

    @Inject
    public ExerciseListDialogFragmentViewModel(ExercisesRepository exercisesRepository) {
        this.exercisesRepository = exercisesRepository;
    }

    LiveData<List<Exercise>> load(long muscleGroupId) {
        if (liveExercises == null) {
            liveExercises = exercisesRepository.getExercisesForMuscleGroup(muscleGroupId);
        }

        return liveExercises;
    }
}
