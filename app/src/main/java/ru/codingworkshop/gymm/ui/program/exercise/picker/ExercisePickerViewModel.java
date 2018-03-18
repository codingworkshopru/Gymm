package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.data.util.LiveDataUtil;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

public class ExercisePickerViewModel extends ViewModel {
    private ExercisesRepository exercisesRepository;
    private MuscleGroupsRepository muscleGroupsRepository;

    private MutableLiveData<MuscleGroup> liveMuscleGroup = new MutableLiveData<>();
    private MutableLiveData<Exercise> liveExercise = new MutableLiveData<>();
    private Map<Boolean, LiveData<List<MuscleGroup>>> muscleGroups = new HashMap<>(2);

    @Inject
    ExercisePickerViewModel(ExercisesRepository exercisesRepository, MuscleGroupsRepository muscleGroupsRepository) {
        this.exercisesRepository = exercisesRepository;
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    LiveData<List<Exercise>> getExercisesForMuscleGroup() {
        return Transformations.switchMap(
                liveMuscleGroup,
                muscleGroup -> muscleGroup != null
                        ? exercisesRepository.getExercisesForMuscleGroup(muscleGroup.getId())
                        : LiveDataUtil.getAbsent());
    }

    void setMuscleGroup(@NonNull MuscleGroup muscleGroup) {
        liveMuscleGroup.setValue(muscleGroup);
    }

    LiveData<MuscleGroup> getMuscleGroup() {
        return liveMuscleGroup;
    }

    void clearMuscleGroup() {
        liveMuscleGroup.setValue(null);
    }

    void setExercise(@NonNull Exercise exercise) {
        liveExercise.setValue(exercise);
    }

    LiveData<Exercise> getExercise() {
        return liveExercise;
    }

    LiveData<List<MuscleGroup>> getMuscleGroups(boolean anterior) {
        if (!muscleGroups.containsKey(anterior)) {
            muscleGroups.put(anterior, muscleGroupsRepository.getMuscleGroups(anterior));
        }
        return muscleGroups.get(anterior);
    }
}
