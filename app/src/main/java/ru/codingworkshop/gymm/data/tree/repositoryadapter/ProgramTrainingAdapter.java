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
 * Created by Radik on 11.11.2017.
 */

@Singleton
public class ProgramTrainingAdapter implements ParentAdapter<ProgramTraining>,
        ChildrenAdapter<ProgramExercise>, GrandchildrenAdapter<ProgramSet> {

    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    @Inject
    public ProgramTrainingAdapter(ProgramTrainingRepository programTrainingRepository, ExercisesRepository exercisesRepository) {
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;
    }

    public LiveData<List<Exercise>> getExercises(long programTrainingId) {
        return exercisesRepository.getExercisesForProgramTraining(programTrainingId);
    }

    @Override
    public LiveData<ProgramTraining> getParent(long id) {
        return programTrainingRepository.getProgramTrainingById(id);
    }

    @Override
    public void updateParent(ProgramTraining item) {
        programTrainingRepository.updateProgramTraining(item);
    }

    @Override
    public void insertParent(ProgramTraining item) {
        programTrainingRepository.insertProgramTraining(item);
    }

    @Override
    public void deleteParent(ProgramTraining item) {
        programTrainingRepository.deleteProgramTraining(item);
    }

    @Override
    public LiveData<List<ProgramExercise>> getChildren(long id) {
        return programTrainingRepository.getProgramExercisesForTraining(id);
    }

    @Override
    public void insertChildren(Collection<ProgramExercise> children) {

    }

    @Override
    public void updateChildren(Collection<ProgramExercise> children) {
        programTrainingRepository.updateProgramExercises(children);
    }

    @Override
    public void deleteChildren(Collection<ProgramExercise> children) {
        programTrainingRepository.deleteProgramExercises(children);
    }

    @Override
    public LiveData<List<ProgramSet>> getGrandchildren(long id) {
        return programTrainingRepository.getProgramSetsForTraining(id);
    }
}
