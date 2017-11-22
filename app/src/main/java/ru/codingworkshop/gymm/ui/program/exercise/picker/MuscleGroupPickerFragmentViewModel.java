package ru.codingworkshop.gymm.ui.program.exercise.picker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

/**
 * Created by Radik on 22.11.2017.
 */

public class MuscleGroupPickerFragmentViewModel extends ViewModel {
    private MuscleGroupsRepository repository;
    private Map<Boolean, LiveData<List<MuscleGroup>>> muscleGroups;

    @Inject
    public MuscleGroupPickerFragmentViewModel(MuscleGroupsRepository repository) {
        this.repository = repository;
        muscleGroups = new HashMap<>(2);
    }

    LiveData<List<MuscleGroup>> load(boolean anterior) {
        if (!muscleGroups.containsKey(anterior)) {
            muscleGroups.put(anterior, repository.getMuscleGroups(anterior));
        }

        return muscleGroups.get(anterior);
    }
}
