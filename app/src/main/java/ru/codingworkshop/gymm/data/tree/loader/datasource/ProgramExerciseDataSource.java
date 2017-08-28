package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.google.common.base.Preconditions;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

public class ProgramExerciseDataSource extends NodeDataSource<ProgramExercise, ProgramSet> {
    private LiveData<Exercise> exercise;

    public ProgramExerciseDataSource(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository, long programExerciseId) {
        setParent(repository.getProgramExerciseById(programExerciseId));
        setChildren(repository.getProgramSetsForExercise(programExerciseId));
        exercise = Transformations.switchMap(getParent(), input ->
                Preconditions.checkNotNull(exercisesRepository.getExerciseById(input.getExerciseId()))
        );
    }

    public LiveData<Exercise> getExercise() {
        return exercise;
    }
}
