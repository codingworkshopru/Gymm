package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;

/**
 * Created by Radik on 21.11.2017.
 */

public class ExercisePickerExerciseListFragmentViewModel extends ViewModel {
    LiveData<List<Exercise>> load(long muscleGroupId) {
        return null;
    }
}
