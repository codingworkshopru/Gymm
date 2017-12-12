package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Radik on 09.11.2017.
 */

public class ProgramExerciseAdapter implements ParentAdapter<ProgramExercise>, ChildrenAdapter<ProgramSet> {
    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    @Inject
    public ProgramExerciseAdapter(ProgramTrainingRepository programTrainingRepository, ExercisesRepository exercisesRepository) {

        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;
    }

    public LiveData<Exercise> getExercise(long programExerciseId) {
        return exercisesRepository.getExerciseForProgramExercise(programExerciseId);
    }

    @Override
    public LiveData<ProgramExercise> getParent(long programExerciseId) {
        return programTrainingRepository.getProgramExerciseById(programExerciseId);
    }

    @Override
    public LiveData<List<ProgramSet>> getChildren(long programExerciseId) {
        return programTrainingRepository.getProgramSetsForExercise(programExerciseId);
    }

    @Override
    public void updateParent(ProgramExercise programExercise) {
        programTrainingRepository.updateProgramExercise(programExercise);
    }

    @Override
    public LiveData<Long> insertParent(ProgramExercise programExercise) {
        programTrainingRepository.insertProgramExercise(programExercise);
        return null;
    }

    @Override
    public void deleteParent(ProgramExercise programExercise) {
        programTrainingRepository.deleteProgramExercise(programExercise);
    }

    @Override
    public LiveData<List<Long>> insertChildren(Collection<ProgramSet> children) {
        programTrainingRepository.insertProgramSets(children);
        return null;
    }

    @Override
    public void updateChildren(Collection<ProgramSet> children) {
        programTrainingRepository.updateProgramSets(children);
    }

    @Override
    public void deleteChildren(Collection<ProgramSet> children) {
        programTrainingRepository.deleteProgramSets(children);
    }
}
