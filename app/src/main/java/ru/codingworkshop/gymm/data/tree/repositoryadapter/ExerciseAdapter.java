package ru.codingworkshop.gymm.data.tree.repositoryadapter;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import ru.codingworkshop.gymm.data.entity.Exercise;
import ru.codingworkshop.gymm.data.entity.MuscleGroup;
import ru.codingworkshop.gymm.repository.ExercisesRepository;
import ru.codingworkshop.gymm.repository.MuscleGroupsRepository;

/**
 * Created by Radik on 17.12.2017.
 */

public class ExerciseAdapter implements ParentAdapter<Exercise>, ChildrenAdapter<MuscleGroup> {
    private ExercisesRepository exercisesRepository;
    private MuscleGroupsRepository muscleGroupsRepository;

    @Inject
    public ExerciseAdapter(ExercisesRepository exercisesRepository, MuscleGroupsRepository muscleGroupsRepository) {
        this.exercisesRepository = exercisesRepository;
        this.muscleGroupsRepository = muscleGroupsRepository;
    }

    public MuscleGroup getPrimaryMuscleGroup(long exerciseId) {
        return muscleGroupsRepository.getPrimaryMuscleGroupForExercise(exerciseId);
    }

    @Override
    public Exercise getParent(long parentId) {
        return exercisesRepository.getExerciseById(parentId);
    }

    @Override
    public long insertParent(Exercise parent) {
        return 0L;
    }

    @Override
    public void updateParent(Exercise parent) {

    }

    @Override
    public void deleteParent(Exercise parent) {

    }

    @Override
    public List<MuscleGroup> getChildren(long parentId) {
        return muscleGroupsRepository.getSecondaryMuscleGroupsForExercise(parentId);
    }

    @Override
    public List<Long> insertChildren(Collection<MuscleGroup> children) {
        return null;
    }

    @Override
    public void updateChildren(Collection<MuscleGroup> children) {

    }

    @Override
    public void deleteChildren(Collection<MuscleGroup> children) {

    }
}
