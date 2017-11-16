package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import android.arch.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

/**
 * Created by Radik on 11.11.2017.
 */

public class ExerciseAdapter implements ParentAdapter<Exercise>, ChildrenAdapter<MuscleGroup> {
    private ExercisesRepository exercisesRepository;
    private MuscleGroupsRepository muscleGroupsRepository;

    @Inject
    public ExerciseAdapter(ExercisesRepository exercisesRepository, MuscleGroupsRepository muscleGroupsRepository) {
        this.exercisesRepository = exercisesRepository;
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    public LiveData<MuscleGroup> getPrimaryMuscleGroup(long exerciseId) {
        return muscleGroupsRepository.getPrimaryMuscleGroupForExercise(exerciseId);
    }

    @Override
    public LiveData<Exercise> getParent(long id) {
        return exercisesRepository.getExerciseById(id);
    }

    @Override
    public void updateParent(Exercise item) {

    }

    @Override
    public void insertParent(Exercise item) {

    }

    @Override
    public void deleteParent(Exercise item) {

    }

    @Override
    public LiveData<List<MuscleGroup>> getChildren(long id) {
        return muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(id);
    }

    @Override
    public void insertChildren(Collection<MuscleGroup> children) {

    }

    @Override
    public void updateChildren(Collection<MuscleGroup> children) {

    }

    @Override
    public void deleteChildren(Collection<MuscleGroup> children) {

    }
}
