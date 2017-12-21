package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.ProgramExercise;
import ru.codingworkshop.gymm.data.entity.ProgramSet;
import ru.codingworkshop.gymm.data.entity.ProgramTraining;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.ProgramTrainingRepository;

/**
 * Created by Radik on 11.11.2017.
 */

public class ProgramTrainingAdapter implements ParentAdapter<ProgramTraining>,
        ChildrenAdapter<ProgramExercise>, GrandchildrenAdapter<ProgramSet> {

    private ProgramTrainingRepository programTrainingRepository;
    private ExercisesRepository exercisesRepository;

    @Inject
    public ProgramTrainingAdapter(ProgramTrainingRepository programTrainingRepository, ExercisesRepository exercisesRepository) {
        this.programTrainingRepository = programTrainingRepository;
        this.exercisesRepository = exercisesRepository;
    }

    public Flowable<List<Exercise>> getExercises(long programTrainingId) {
        return exercisesRepository.getExercisesForProgramTraining(programTrainingId);
    }

    public LiveData<ProgramTraining> getProgramTrainingByName(String programTrainingName) {
        return programTrainingRepository.getProgramTrainingByName(programTrainingName);
    }

    @Override
    public Flowable<ProgramTraining> getParent(long id) {
        return programTrainingRepository.getProgramTrainingById(id);
    }

    @Override
    public void updateParent(ProgramTraining item) {
        programTrainingRepository.updateProgramTraining(item);
    }

    @Override
    public long insertParent(ProgramTraining item) {
        return programTrainingRepository.insertProgramTraining(item);
    }

    @Override
    public void deleteParent(ProgramTraining item) {
        programTrainingRepository.deleteProgramTraining(item);
    }

    @Override
    public Flowable<List<ProgramExercise>> getChildren(long id) {
        return programTrainingRepository.getProgramExercisesForTraining(id);
    }

    @Override
    public List<Long> insertChildren(Collection<ProgramExercise> children) {
        return programTrainingRepository.insertProgramExercises(children);
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
    public Flowable<List<ProgramSet>> getGrandchildren(long id) {
        return programTrainingRepository.getProgramSetsForTraining(id);
    }

    @Override
    public void insertGrandchildren(Collection<ProgramSet> grandchildren) {
        programTrainingRepository.insertProgramSets(grandchildren);
    }

    @Override
    public void updateGrandchildren(Collection<ProgramSet> grandchildren) {
        programTrainingRepository.updateProgramSets(grandchildren);
    }

    @Override
    public void deleteGrandchildren(Collection<ProgramSet> grandchildren) {
        programTrainingRepository.deleteProgramSets(grandchildren);
    }
}
