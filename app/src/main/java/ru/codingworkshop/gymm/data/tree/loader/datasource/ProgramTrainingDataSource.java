package ru.codingworkshop.gymm.data.tree.loader.datasource;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Радик on 24.08.2017 as part of the Gymm project.
 */

public class ProgramTrainingDataSource extends TreeDataSource<ProgramTraining, ProgramExercise, ProgramSet> {
    private LiveData<List<Exercise>> exercises;

    public ProgramTrainingDataSource(ProgramTrainingRepository repository, ExercisesRepository exercisesRepository, long programTrainingId) {
        setParent(repository.getProgramTrainingById(programTrainingId));
        setChildren(repository.getProgramExercisesForTraining(programTrainingId));
        setGrandchildren(repository.getProgramSetsForTraining(programTrainingId));
        exercises = exercisesRepository.getExercisesForProgramTraining(programTrainingId);
    }

    public LiveData<List<Exercise>> getExercises() {
        return exercises;
    }
}
